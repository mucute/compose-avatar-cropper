package cn.mucute.compose.avatar.cropper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntRect

actual fun cropImage(
    imageBitmap: ImageBitmap,
    cropRect: IntRect,
    outputSize: Int,
    isCircle: Boolean
): ImageBitmap {
    val srcBitmap = imageBitmap.asAndroidBitmap()
    
    // 确保裁剪区域在图片范围内
    val safeLeft = cropRect.left.coerceAtLeast(0)
    val safeTop = cropRect.top.coerceAtLeast(0)
    val safeRight = cropRect.right.coerceAtMost(srcBitmap.width)
    val safeBottom = cropRect.bottom.coerceAtMost(srcBitmap.height)
    
    val width = safeRight - safeLeft
    val height = safeBottom - safeTop
    
    if (width <= 0 || height <= 0) {
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).asImageBitmap()
    }
    
    // 裁剪矩形
    val croppedBitmap = Bitmap.createBitmap(srcBitmap, safeLeft, safeTop, width, height)
    
    if (!isCircle) {
        return croppedBitmap.asImageBitmap()
    }
    
    // 如果是圆形，创建一个正方形的 Bitmap 并绘制圆形
    val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(outputBitmap)
    val paint = Paint().apply {
        isAntiAlias = true
    }
    
    // 绘制圆形遮罩
    canvas.drawCircle(width / 2f, height / 2f, minOf(width, height) / 2f, paint)
    
    // 设置混合模式为 SRC_IN，只保留重叠部分的源图像
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(croppedBitmap, 0f, 0f, paint)
    
    return outputBitmap.asImageBitmap()
}
