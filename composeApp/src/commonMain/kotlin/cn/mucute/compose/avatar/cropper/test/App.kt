package cn.mucute.compose.avatar.cropper.test

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import cn.mucute.compose.avatar.cropper.AvatarCropper
import cn.mucute.compose.avatar.cropper.CropShape
import cn.mucute.compose.avatar.cropper.rememberCropState

import compose_avatar_cropper.composeapp.generated.resources.Res
import compose_avatar_cropper.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        var selectedImage by remember { mutableStateOf<ImageBitmap?>(null) }
        var croppedImage by remember { mutableStateOf<ImageBitmap?>(null) }
        var showCropper by remember { mutableStateOf(false) }

        val imagePicker = rememberImagePicker {
            selectedImage = it
            showCropper = true
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .safeContentPadding()
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onClick = { imagePicker.pickImage() }) {
                    Text("Select Image")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (croppedImage != null) {
                    Text("Cropped Result:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        bitmap = croppedImage!!,
                        contentDescription = "Cropped Image",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { croppedImage = null }) {
                        Text("Clear")
                    }
                } else {
                    Button(onClick = { showContent = !showContent }) {
                        Text("Click me!")
                    }
                    AnimatedVisibility(showContent) {
                        val greeting = remember { Greeting().greet() }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(painterResource(Res.drawable.compose_multiplatform), null)
                            Text("Compose: $greeting")
                        }
                    }
                }
            }

            if (showCropper && selectedImage != null) {
                val cropState = rememberCropState()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .clickable(enabled = true) {} // Prevent clicking through
                ) {
                    AvatarCropper(
                        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp),
                        imageBitmap = selectedImage!!,
                        state = cropState,
                        shape = CropShape.Circle
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(onClick = { showCropper = false }) {
                            Text("Cancel")
                        }
                        Button(onClick = {
                            val result = cropState.crop(selectedImage!!)
                            if (result != null) {
                                croppedImage = result
                                showCropper = false
                            }
                        }) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}