plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.dieyteixeira.componentes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dieyteixeira.componentes"
        minSdk = 24
        targetSdk = 34
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Extension Icons
    implementation(libs.androidx.material.icons.extended)

    // Rich Text Editor
    implementation(libs.richeditor.compose)

    // Coil
    implementation(libs.coil.compose)

    // ViewModel em Jetpack Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // WebView Accompanist library
    implementation(libs.accompanist.webview)

    // Conexão IA
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Animação Sonora
    implementation(libs.ui)
    implementation(libs.androidx.animation)
    implementation(libs.material3)
    implementation(libs.androidx.media)
    implementation(libs.androidx.foundation)
}