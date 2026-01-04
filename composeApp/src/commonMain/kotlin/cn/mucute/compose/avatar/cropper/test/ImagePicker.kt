package cn.mucute.compose.avatar.cropper.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun rememberImagePicker(onImagePicked: (ImageBitmap) -> Unit): ImagePicker

interface ImagePicker {
    fun pickImage()
}
