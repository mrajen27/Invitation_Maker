import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.vaangainvite"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.vaangainvite"
        minSdk = 26
        targetSdk = 35
        versionCode = 18
        versionName = "1.0.17"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("ANDROID_KEYSTORE_PATH")
            if (!keystorePath.isNullOrBlank()) {
                storeFile = rootProject.file(keystorePath)
                storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
                    ?: error("ANDROID_KEYSTORE_PASSWORD is required for release signing")
                keyAlias = System.getenv("ANDROID_KEY_ALIAS")
                    ?: error("ANDROID_KEY_ALIAS is required for release signing")
                keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
                    ?: storePassword
            } else {
                val keystorePropertiesFile = rootProject.file("keystore.properties")
                if (keystorePropertiesFile.exists()) {
                    val props = Properties()
                    keystorePropertiesFile.inputStream().use { props.load(it) }
                    storeFile = rootProject.file(
                        props.getProperty("storeFile")
                            ?: error("storeFile is missing in keystore.properties"),
                    )
                    storePassword = props.getProperty("storePassword")
                        ?: error("storePassword is missing in keystore.properties")
                    keyAlias = props.getProperty("keyAlias")
                        ?: error("keyAlias is missing in keystore.properties")
                    keyPassword = props.getProperty("keyPassword") ?: storePassword
                }
            }
        }
    }

    buildTypes {
        release {
            signingConfigs.getByName("release").takeIf { it.storeFile?.exists() == true }?.let {
                signingConfig = it
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("com.google.mlkit:face-detection:16.1.7")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
