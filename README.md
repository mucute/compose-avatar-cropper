# Compose Avatar Cropper

[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-blue.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![Platform](https://img.shields.io/badge/platform-Android%20%7C%20Desktop-lightgrey.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](./LICENSE)

[English](#english) | [中文](#chinese)

<a name="english"></a>
## English

**Compose Avatar Cropper** is a lightweight and powerful image cropping library built with Kotlin Multiplatform and Jetpack Compose. It is designed for selecting and cropping user avatars with support for touch gestures.

### Features

- 🚀 **Multiplatform Support**: Works seamlessly on Android and Desktop (JVM).
- 👆 **Gestures**: Supports pinch-to-zoom, pan, and double-tap to reset/zoom.
- 🎨 **Custom Shapes**: Supports both **Circle** and **Square** cropping areas.
- 🔧 **Easy Integration**: Simple API designed for Compose.
- 🎭 **Smooth Animations**: Built-in animations for state changes.

### Installation

This library is available on Maven Central.

Add the dependency to your `build.gradle.kts` (commonMain):

```kotlin
commonMain.dependencies {
    implementation("cn.mucute.compose:avatar-cropper:1.0.0")
}
```

### Usage

#### 1. Basic Implementation

Use the `AvatarCropper` composable to display the cropping interface.

```kotlin
import cn.mucute.compose.avatar.cropper.AvatarCropper
import cn.mucute.compose.avatar.cropper.CropShape
import cn.mucute.compose.avatar.cropper.rememberCropState

@Composable
fun MyCropperScreen(imageBitmap: ImageBitmap) {
    // 1. Initialize the state
    val cropState = rememberCropState()

    // 2. Render the cropper
    AvatarCropper(
        imageBitmap = imageBitmap,
        state = cropState,
        shape = CropShape.Circle, // Options: CropShape.Circle or CropShape.Square
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.Black // Optional background color
    )
    
    // 3. Button to trigger crop
    Button(onClick = {
        val result = cropState.crop(imageBitmap)
        // result is the cropped ImageBitmap
    }) {
        Text("Crop Image")
    }
}
```

#### 2. API Reference

**`AvatarCropper`**

| Parameter | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `imageBitmap` | `ImageBitmap` | **Required** | The source image to be cropped. |
| `state` | `CropState` | `rememberCropState()` | State object to control cropping logic. |
| `shape` | `CropShape` | `CropShape.Square` | The shape of the cropping mask (`Circle` or `Square`). |
| `backgroundColor` | `Color` | `Color.Transparent` | Background color behind the image. |
| `modifier` | `Modifier` | `Modifier` | Modifier for the layout. |

**`CropState`**

- `crop(imageBitmap: ImageBitmap): ImageBitmap?`: Performs the crop operation and returns the result. Returns `null` if the crop area is invalid.
- `reset(imageBitmap: ImageBitmap)`: Resets the image scale and position to fit the screen.
- `updateShape(newShape: CropShape, imageBitmap: ImageBitmap)`: Dynamically changes the crop shape.

---

<a name="chinese"></a>
## 中文

**Compose Avatar Cropper** 是一个基于 Kotlin Multiplatform 和 Jetpack Compose 构建的轻量级且强大的图片裁剪库。专为用户头像选择和裁剪设计，支持流畅的手势操作。

### 功能特性

- 🚀 **多平台支持**: 完美支持 Android 和 Desktop (JVM)。
- 👆 **手势操作**: 支持双指缩放、拖拽移动以及双击复位/放大。
- 🎨 **自定义形状**: 支持 **圆形 (Circle)** 和 **方形 (Square)** 裁剪区域。
- 🔧 **易于集成**: 专为 Compose 设计的简洁 API。
- 🎭 **流畅动画**: 内置状态切换动画。

### 引入教程

该库已发布到 Maven Central。

在您的 `build.gradle.kts` (commonMain) 中添加依赖：

```kotlin
commonMain.dependencies {
    implementation("cn.mucute.compose:avatar-cropper:1.0.0")
}
```

### 使用文档

#### 1. 基础实现

使用 `AvatarCropper` 组件来显示裁剪界面。

```kotlin
import cn.mucute.compose.avatar.cropper.AvatarCropper
import cn.mucute.compose.avatar.cropper.CropShape
import cn.mucute.compose.avatar.cropper.rememberCropState

@Composable
fun MyCropperScreen(imageBitmap: ImageBitmap) {
    // 1. 初始化状态
    val cropState = rememberCropState()

    // 2. 渲染裁剪组件
    AvatarCropper(
        imageBitmap = imageBitmap,
        state = cropState,
        shape = CropShape.Circle, // 选项: CropShape.Circle 或 CropShape.Square
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.Black // 可选背景色
    )
    
    // 3. 触发裁剪的按钮
    Button(onClick = {
        val result = cropState.crop(imageBitmap)
        // result 即为裁剪后的 ImageBitmap
    }) {
        Text("裁剪图片")
    }
}
```

#### 2. API 参考

**`AvatarCropper`**

| 参数 | 类型 | 默认值 | 描述 |
| :--- | :--- | :--- | :--- |
| `imageBitmap` | `ImageBitmap` | **必填** | 需要裁剪的源图片。 |
| `state` | `CropState` | `rememberCropState()` | 控制裁剪逻辑的状态对象。 |
| `shape` | `CropShape` | `CropShape.Square` | 裁剪遮罩的形状 (`Circle` 或 `Square`)。 |
| `backgroundColor` | `Color` | `Color.Transparent` | 图片背后的背景颜色。 |
| `modifier` | `Modifier` | `Modifier` | 布局修饰符。 |

**`CropState`**

- `crop(imageBitmap: ImageBitmap): ImageBitmap?`: 执行裁剪操作并返回结果。如果裁剪区域无效则返回 `null`。
- `reset(imageBitmap: ImageBitmap)`: 重置图片缩放和位置以适应屏幕。
- `updateShape(newShape: CropShape, imageBitmap: ImageBitmap)`: 动态更改裁剪形状。

## License

```
Copyright 2024 mucute

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
