import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.junit5)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "woowacourse.shopping"
    compileSdk = 35

    val localProperties =
        gradle.rootProject
            .file("local.properties")
            .inputStream()
            .use { Properties().apply { load(it) } }

    val authUsername = localProperties["auth.username"] as String
    val authPassword = localProperties["auth.password"] as String
    val baseUrl = localProperties["BASE_URL"] as String

    defaultConfig {
        buildConfigField("String", "AUTH_USERNAME", "\"$authUsername\"")
        buildConfigField("String", "AUTH_PASSWORD", "\"$authPassword\"")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }

    defaultConfig {
        applicationId = "woowacourse.shopping"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    packaging {
        resources {
            excludes += "META-INF/**"
            excludes += "win32-x86*/**"
        }
    }
    dataBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.google.material)
    implementation(libs.glide)
    implementation(libs.androidx.room.runtime)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.mockwebserver)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    kapt(libs.androidx.room.compiler)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.json)
    debugImplementation(libs.androidx.fragment.testing.manifest)
    androidTestImplementation(libs.androidx.fragment.testing)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.assertj.core)
    androidTestImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.kotest.runner.junit5)
    androidTestImplementation(libs.mannodermaus.junit5.core)
    androidTestRuntimeOnly(libs.mannodermaus.junit5.runner)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.shimmer)
}
