import java.util.Properties

plugins {
    // AGP 9 compiles Kotlin itself (built-in Kotlin), so the standalone
    // `org.jetbrains.kotlin.android` plugin must NOT be applied here.
    alias(libs.plugins.android.application)
    // The Compose compiler plugin still has to be applied explicitly.
    alias(libs.plugins.kotlin.compose)
}

/**
 * Release signing material.
 *
 * Locally it is read from `keystore.properties` (git-ignored); on CI it comes from
 * environment variables backed by GitHub Actions secrets. When none of it is present
 * the release build still succeeds and simply emits an unsigned APK.
 */
val signingProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

fun signingValue(key: String): String? = signingProperties.getProperty(key) ?: System.getenv(key)

val keystorePath = signingValue("KEYSTORE_PATH")
val keystorePassword = signingValue("KEYSTORE_PASSWORD")
val releaseKeyAlias = signingValue("KEY_ALIAS")
val releaseKeyPassword = signingValue("KEY_PASSWORD")
val hasReleaseSigning =
    keystorePath != null && keystorePassword != null && releaseKeyAlias != null && releaseKeyPassword != null

android {
    namespace = "top.youzix.nekoedit"
    compileSdk = 37
    buildToolsVersion = "37.0.0"

    defaultConfig {
        applicationId = "top.youzix.nekoedit"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            register("release") {
                storeFile = rootProject.file(keystorePath!!)
                storePassword = keystorePassword!!
                keyAlias = releaseKeyAlias!!
                keyPassword = releaseKeyPassword!!
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

// `kotlin.compilerOptions.jvmTarget` is not set on purpose: with built-in Kotlin it
// already defaults to `android.compileOptions.targetCompatibility` (Java 21).

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.foundation)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)

    implementation(libs.miuix.ui)
    implementation(libs.miuix.preference)
    implementation(libs.miuix.icons)
}
