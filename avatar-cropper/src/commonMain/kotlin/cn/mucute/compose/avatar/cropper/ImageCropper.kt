package cn.mucute.compose.avatar.cropper

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class CropShape {
    Square, Circle
}

@Stable
class CropState(
    initialShape: CropShape = CropShape.Square
) {
    var shape by mutableStateOf(initialShape)
    
    var scale by mutableStateOf(1f)
        internal set
    var offset by mutableStateOf(Offset.Zero)
        internal set
        
    // Internal state for logic
    internal var containerSize by mutableStateOf(IntSize.Zero)
    internal var cropSize by mutableStateOf(0f) // Side length of the square crop area
    internal var minScale by mutableStateOf(1f)
    
    // Animation
    internal var animationJob: Job? = null

    // Calculated property for the crop rectangle on screen
    val cropRectScreen: Rect
        get() {
            if (containerSize.width == 0 || containerSize.height == 0 || cropSize == 0f) {
                return Rect.Zero
            }
            val cx = containerSize.width / 2f
            val cy = containerSize.height / 2f
            return Rect(
                left = cx - cropSize / 2f,
                top = cy - cropSize / 2f,
                right = cx + cropSize / 2f,
                bottom = cy + cropSize / 2f
            )
        }

    fun crop(imageBitmap: ImageBitmap): ImageBitmap? {
        if (containerSize.width <= 0 || containerSize.height <= 0 || cropRectScreen.isEmpty) {
            return null
        }
        
        // Map screen coordinates to image coordinates
        val leftOnImg = (cropRectScreen.left - offset.x) / scale
        val topOnImg = (cropRectScreen.top - offset.y) / scale
        val rightOnImg = (cropRectScreen.right - offset.x) / scale
        val bottomOnImg = (cropRectScreen.bottom - offset.y) / scale
        
        val cropRect = IntRect(
            leftOnImg.roundToInt(),
            topOnImg.roundToInt(),
            rightOnImg.roundToInt(),
            bottomOnImg.roundToInt()
        )
        
        val outputSize = if (shape == CropShape.Circle) {
            (cropRectScreen.width / scale).toInt()
        } else {
            0
        }
        
        return cropImage(
            imageBitmap,
            cropRect,
            outputSize,
            shape == CropShape.Circle
        )
    }
    
    // Helper to reset view to default (fit center)
    fun reset(imageBitmap: ImageBitmap) {
        if (containerSize.width > 0 && containerSize.height > 0) {
            val srcW = imageBitmap.width.toFloat()
            val srcH = imageBitmap.height.toFloat()
            val w = containerSize.width.toFloat()
            val h = containerSize.height.toFloat()
            
            // Fit logic
            val fitScale = minOf(w / srcW, h / srcH) * 0.9f
            
            // Reset crop size based on shape preference? 
            // Usually reset implies fitting the image.
            // For Square/Circle, we take the smaller dimension to fit the crop area.
            val size = minOf(srcW, srcH) * fitScale
            
            cropSize = size
            minScale = fitScale
            scale = fitScale
            
            val displayW = srcW * fitScale
            val displayH = srcH * fitScale
            offset = Offset(
                (w - displayW) / 2f,
                (h - displayH) / 2f
            )
        }
    }
    
    // Update shape and adjust crop area if needed
    fun updateShape(newShape: CropShape, imageBitmap: ImageBitmap) {
        shape = newShape
        // For Square/Circle switching, the logic is similar (both are 1:1 aspect ratio).
        // If we were supporting Rectangle, we would need more complex logic.
        // But we want to ensure constraints are met.
        if (cropSize > 0) {
             // Re-verify constraints
             val srcW = imageBitmap.width.toFloat()
             val srcH = imageBitmap.height.toFloat()
             // Recalculate minScale
             val newMinScale = maxOf(cropSize / srcW, cropSize / srcH)
             minScale = newMinScale
             if (scale < minScale) scale = minScale
             
             offset = clampOffset(offset, scale, cropRectScreen, srcW, srcH)
        }
    }

    internal fun clampOffset(proposedOffset: Offset, currentScale: Float, cropRect: Rect, srcW: Float, srcH: Float): Offset {
        if (cropRect.isEmpty) return proposedOffset
        
        val displayW = srcW * currentScale
        val displayH = srcH * currentScale
        
        val maxX = cropRect.left
        val minX = cropRect.right - displayW
        
        val maxY = cropRect.top
        val minY = cropRect.bottom - displayH
        
        val safeMinX = if (minX > maxX) maxX else minX
        val safeMinY = if (minY > maxY) maxY else minY
        
        return Offset(
            proposedOffset.x.coerceIn(safeMinX, maxX),
            proposedOffset.y.coerceIn(safeMinY, maxY)
        )
    }
}

@Composable
fun rememberCropState(initialShape: CropShape = CropShape.Square): CropState {
    return remember { CropState(initialShape) }
}

@Composable
fun AvatarCropper(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    state: CropState = rememberCropState(),
    shape: CropShape = CropShape.Square,
    backgroundColor: Color = Color.Transparent
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    // Sync shape from parameter to state
    LaunchedEffect(shape, imageBitmap) {
        state.updateShape(shape, imageBitmap)
    }

    // Reset initialization when image changes
    LaunchedEffect(imageBitmap) {
        state.reset(imageBitmap)
    }

    BoxWithConstraints(
        modifier
            .background(backgroundColor)
            .clip(RectangleShape)
            .pointerInput(state.cropRectScreen) {
                detectTapGestures(
                    onDoubleTap = { tapCenter ->
                        state.animationJob?.cancel()
                        
                        val currentScale = state.scale
                        val currentOffset = state.offset
                        val targetScale: Float
                        var targetOffset: Offset
                        
                        if (state.scale > state.minScale * 1.5f) {
                            targetScale = state.minScale
                            // Ideal center
                            val srcW = imageBitmap.width.toFloat()
                            val srcH = imageBitmap.height.toFloat()
                            val displayW = srcW * targetScale
                            val displayH = srcH * targetScale
                            val idealX = (state.containerSize.width - displayW) / 2f
                            val idealY = (state.containerSize.height - displayH) / 2f
                            targetOffset = Offset(idealX, idealY)
                            targetOffset = state.clampOffset(targetOffset, targetScale, state.cropRectScreen, srcW, srcH)
                        } else {
                            val targetS = state.minScale * 3f
                            targetScale = targetS.coerceIn(0.1f, 10f)
                            targetOffset = tapCenter - (tapCenter - state.offset) / currentScale * targetScale
                            targetOffset = state.clampOffset(targetOffset, targetScale, state.cropRectScreen, imageBitmap.width.toFloat(), imageBitmap.height.toFloat())
                        }
                        
                        state.animationJob = coroutineScope.launch {
                            val anim = Animatable(0f)
                            anim.animateTo(1f, animationSpec = tween(durationMillis = 300)) {
                                state.scale = currentScale + (targetScale - currentScale) * value
                                state.offset = lerp(currentOffset, targetOffset, value)
                            }
                        }
                    }
                )
            }
            .pointerInput(state.cropRectScreen) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    if (state.animationJob?.isActive == true) state.animationJob?.cancel()
                    
                    val oldScale = state.scale
                    val newScale = (state.scale * zoom).coerceIn(state.minScale, 10f)
                    val newOffset = centroid - (centroid - state.offset) / oldScale * newScale + pan
                    
                    state.scale = newScale
                    state.offset = state.clampOffset(newOffset, newScale, state.cropRectScreen, imageBitmap.width.toFloat(), imageBitmap.height.toFloat())
                }
            }
            .pointerInput(state.cropRectScreen) {
                 awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Scroll && event.changes.isNotEmpty()) {
                            if (state.animationJob?.isActive == true) state.animationJob?.cancel()
                            
                            val change = event.changes.first()
                            val scrollDelta = change.scrollDelta
                            val zoomFactor = 1.1f
                            val zoomChange = if (scrollDelta.y < 0) zoomFactor else 1 / zoomFactor
                            
                            val oldScale = state.scale
                            val newScale = (state.scale * zoomChange).coerceIn(state.minScale, 10f)
                            val mousePos = change.position
                            val newOffset = mousePos - (mousePos - state.offset) / oldScale * newScale
                            
                            state.scale = newScale
                            state.offset = state.clampOffset(newOffset, newScale, state.cropRectScreen, imageBitmap.width.toFloat(), imageBitmap.height.toFloat())
                            change.consume()
                        }
                    }
                }
            }
    ) {
        val w = constraints.maxWidth
        val h = constraints.maxHeight
        
        // Update container size
        LaunchedEffect(w, h) {
            val newSize = IntSize(w, h)
            if (state.containerSize != newSize) {
                state.containerSize = newSize
                if (state.scale == 1f && state.offset == Offset.Zero) {
                     state.reset(imageBitmap)
                }
            }
        }
        
        Canvas(Modifier.fillMaxSize()) {
            withTransform({
                translate(state.offset.x, state.offset.y)
                scale(state.scale, state.scale, pivot = Offset.Zero)
            }) {
                drawImage(imageBitmap)
            }
            
            // Draw overlay
            val currentRect = state.cropRectScreen
            if (!currentRect.isEmpty) {
                val canvas = drawContext.canvas
                canvas.saveLayer(Rect(0f, 0f, size.width, size.height), Paint())
                try {
                    drawRect(color = Color.Black.copy(alpha = 0.5f))
                    
                    if (state.shape == CropShape.Circle) {
                        drawCircle(
                            color = Color.Transparent,
                            center = currentRect.center,
                            radius = currentRect.width / 2,
                            blendMode = BlendMode.Clear
                        )
                        drawCircle(
                            color = Color.White,
                            center = currentRect.center,
                            radius = currentRect.width / 2,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    } else {
                        drawRect(
                            color = Color.Transparent,
                            topLeft = currentRect.topLeft,
                            size = currentRect.size,
                            blendMode = BlendMode.Clear
                        )
                        drawRect(
                            color = Color.White,
                            topLeft = currentRect.topLeft,
                            size = currentRect.size,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                } finally {
                    canvas.restore()
                }
            }
        }
    }
}
