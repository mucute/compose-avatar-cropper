
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("com.vanniktech.maven.publish")
    signing
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "cn.mucute.compose.avatar.cropper"
        compileSdk = 36
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    jvm(){

    }
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }

}


tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).apply {
        addBooleanOption("Xdoclint:none", true)
        links("https://docs.oracle.com/en/java/javase/17/docs/api/")
        isVersion = true
        isAuthor = true
        charSet = "UTF-8"
    }
}

mavenPublishing {

    coordinates("cn.mucute", "compose-avatar-cropper", "1.0.0")

    pom {
        name.set("compose-avatar-cropper")
        description.set("Compose Avatar Cropper is a lightweight and powerful image cropping library built with Kotlin Multiplatform and Jetpack Compose. It is designed for selecting and cropping user avatars with support for touch gestures.")
        url.set("https://github.com/mucute/compose-avatar-cropper")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                name.set("mucute")
                url.set("https://github.com/mucute")
                email.set("mucute1215@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/mucute/compose-avatar-cropper.git")
            developerConnection.set("scm:git:ssh://github.com/mucute/compose-avatar-cropper.git")
            url.set("https://github.com/mucute/compose-avatar-cropper.git")
        }
    }


    publishToMavenCentral()

    signAllPublications()
}