import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

android {
    namespace = "apero.quanta.picai"
    compileSdk = 36

    defaultConfig {
        applicationId = "apero.quanta.picai"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SERVICE_API", "\"${localProperties.getProperty("service-api", "")}\"")
        buildConfigField("String", "AUTH_API", "\"${localProperties.getProperty("auth-api", "")}\"")
        buildConfigField("String", "KEY_ID", "\"${localProperties.getProperty("key-id", "")}\"")
        buildConfigField("String", "PUBLIC_KEY", "\"${localProperties.getProperty("public-key", "")}\"")
        buildConfigField("String", "BUNDLE_ID", "\"${localProperties.getProperty("bundle-id", "")}\"")
        buildConfigField("String", "HEADER_APP_NAME", "\"${localProperties.getProperty("header-app-name", "")}\"")
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

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.bundles.networking)
    implementation(libs.bundles.hilt)

    implementation(libs.bundles.nav3)

    implementation(libs.api.signature)

    implementation(libs.timber)

    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)

    // Proto DataStore
    implementation(libs.androidx.datastore.core)

    // image loading
    implementation(libs.bundles.image.loading)

    ksp(libs.dagger.hilt.compile)
    ksp(libs.androidx.room.compiler)
    ksp("org.jetbrains.kotlin:kotlin-reflect:2.3.0")


    debugImplementation(libs.androidx.compose.ui.test.manifest)
}