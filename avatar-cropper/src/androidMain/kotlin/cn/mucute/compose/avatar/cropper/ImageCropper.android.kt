package cn.mucute.compose.avatar.cropper

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AvatarCropper(
    modifier: Modifier = Modifier,
    uri: Uri,
    state: CropState = rememberCropState(),
    shape: CropShape = CropShape.Square,
    backgroundColor: Color = Color.Transparent
) {
    val context = LocalContext.current
    var imageBitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uri) {
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    imageBitmap = BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    if (imageBitmap != null) {
        AvatarCropper(
            modifier = modifier,
            imageBitmap = imageBitmap!!,
            state = state,
            shape = shape,
            backgroundColor = backgroundColor
        )
    }
}
