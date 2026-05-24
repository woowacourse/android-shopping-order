import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
}

android {
    namespace = "woowacourse.shopping"
    compileSdk = 36

    defaultConfig {
        applicationId = "woowacourse.shopping"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        val testUsername = properties.getProperty("TEST_USERNAME") ?: ""
        val testPassword = properties.getProperty("TEST_PASSWORD") ?: ""
        val baseUrl = properties.getProperty("BASE_URL") ?: ""

        buildConfigField("String", "TEST_USERNAME", "\"$testUsername\"")
        buildConfigField("String", "TEST_PASSWORD", "\"$testPassword\"")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.datastore.tink)
    ksp(libs.androidx.room.compiler)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.tink.android)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.assertj.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.json)

    testImplementation(libs.mockwebserver)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
