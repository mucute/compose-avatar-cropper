package cn.mucute.compose.avatar.cropper

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntRect

expect fun cropImage(
    imageBitmap: ImageBitmap,
    cropRect: IntRect,
    outputSize: Int, // 仅用于圆形裁剪时可能需要的缩放，或者我们直接返回裁剪后的原始分辨率
    isCircle: Boolean
): ImageBitmap


sealed class ImageSource {
    data class File(val path: String) : ImageSource()
    data class Uri(val uri: String) : ImageSource() // Android specific usually
}
