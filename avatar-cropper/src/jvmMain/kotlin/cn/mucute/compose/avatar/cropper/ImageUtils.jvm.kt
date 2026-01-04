package cn.mucute.compose.avatar.cropper

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.IntRect
import java.awt.RenderingHints
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage

actual fun cropImage(
    imageBitmap: ImageBitmap,
    cropRect: IntRect,
    outputSize: Int,
    isCircle: Boolean
): ImageBitmap {
    val awtImage = imageBitmap.toAwtImage()
    
    // 边界检查
    val safeLeft = cropRect.left.coerceAtLeast(0)
    val safeTop = cropRect.top.coerceAtLeast(0)
    val safeRight = cropRect.right.coerceAtMost(awtImage.width)
    val safeBottom = cropRect.bottom.coerceAtMost(awtImage.height)
    
    val width = safeRight - safeLeft
    val height = safeBottom - safeTop
    
    if (width <= 0 || height <= 0) {
         return BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).toComposeImageBitmap()
    }
    
    val croppedImage = awtImage.getSubimage(safeLeft, safeTop, width, height)
    
    if (!isCircle) {
        return croppedImage.toComposeImageBitmap()
    }
    
    // 圆形裁剪
    val outputImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g2 = outputImage.createGraphics()
    
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    
    // 创建圆形剪切区
    val circle = Ellipse2D.Float(0f, 0f, width.toFloat(), height.toFloat())
    g2.clip(circle)
    
    g2.drawImage(croppedImage, 0, 0, null)
    g2.dispose()
    
    return outputImage.toComposeImageBitmap()
}
