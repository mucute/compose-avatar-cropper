package cn.mucute.compose.avatar.cropper.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.imageio.ImageIO

@Composable
actual fun rememberImagePicker(onImagePicked: (ImageBitmap) -> Unit): ImagePicker {
    return remember {
        object : ImagePicker {
            override fun pickImage() {
                val fileDialog = FileDialog(null as Frame?, "Select Image", FileDialog.LOAD)
                fileDialog.isVisible = true
                val file = fileDialog.file
                val directory = fileDialog.directory
                if (file != null && directory != null) {
                    try {
                        val bufferedImage = ImageIO.read(File(directory, file))
                        if (bufferedImage != null) {
                            onImagePicked(bufferedImage.toComposeImageBitmap())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
