plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")

    // Опционально, если нужны:
    // alias(libs.plugins.firebase.crashlytics)
    // alias(libs.plugins.firebase.perf)

}

android {
    namespace = "com.example.fooddiary"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.fooddiary"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Firebase BOM - управляет версиями
//    implementation(platform(libs.firebase.bom))
//
//    implementation(libs.firebase.auth)
//    implementation(libs.firebase.firestore)
//
//    implementation(libs.kotlinx.coroutines.android)
//
//    // Also add the dependencies for the Credential Manager libraries and specify their versions
//    implementation("androidx.credentials:credentials:1.3.0")
//    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
//    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    implementation(libs.lifecycle.viewmodel.compose)

    // CameraX (ИСПРАВЛЕННЫЕ ВЕРСИИ)
    val cameraxVersion = "1.3.2"  // Более стабильная версия
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")

    // Accompanist для разрешений
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // Coil для загрузки изображений
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended:1.6.7")

    // Для решения проблемы с ListenableFuture
    implementation("com.google.guava:guava:31.1-android")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
//    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}