plugins {
    id("com.android.application")
}

android {
    namespace = "se.jonsolberg.sourdough"
    compileSdk = 35

    defaultConfig {
        applicationId = "se.jonsolberg.sourdough"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
