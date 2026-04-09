import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "com.example.piliplus"
    compileSdk = 34

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    // 👇 必须加这个 flavor 维度
    flavorDimensions += "device"

    defaultConfig {
        applicationId = "com.example.piliplus"
        minSdk = 21
        targetSdk = 34
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    packagingOptions.jniLibs.useLegacyPackaging = true

    val keyProperties = Properties().also {
        val properties = rootProject.file("key.properties")
        if (properties.exists())
            it.load(properties.inputStream())
    }

    val config = keyProperties.getProperty("storeFile")?.let {
        signingConfigs.create("release") {
            storeFile = file(it)
            storePassword = keyProperties.getProperty("storePassword")
            keyAlias = keyProperties.getProperty("keyAlias")
            keyPassword = keyProperties.getProperty("keyPassword")
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        all {
            signingConfig = config ?: signingConfigs["debug"]
        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            if (project.hasProperty("dev")) {
                applicationIdSuffix = ".dev"
                resValue("string", "app_name", "PiliPlus dev")
            }
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    productFlavors {
        create("tv") {
            dimension = "device"
            applicationIdSuffix = ".tv"
            versionNameSuffix = "-tv"
            resValue("string", "app_name", "PiliPlus TV")
        }
    }

    applicationVariants.all {
        outputs.forEach { output ->
            (output as ApkVariantOutputImpl).versionCodeOverride = flutter.versionCode
        }
    }
}

flutter {
    source = "../.."
}
