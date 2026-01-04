package cn.mucute.compose.avatar.cropper.test

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform