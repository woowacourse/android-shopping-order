import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("de.mannodermaus.android-junit5") version "1.10.0.0"
    id("kotlin-kapt")
    kotlin("plugin.serialization")
}

android {
    namespace = "woowacourse.shopping"
    compileSdk = 34

    val properties =
        Properties().apply {
            load(FileInputStream(rootProject.file("local.properties")))
        }

    defaultConfig {
        applicationId = "woowacourse.shopping"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] =
            "de.mannodermaus.junit5.AndroidJUnit5Builder"

        buildConfigField("String", "BASE_URL", properties["base_url"] as String)
        buildConfigField("String", "TOKEN", properties["token"] as String)
        buildConfigField("String", "USERNAME", properties["user_name"] as String)
        buildConfigField("String", "PASSWORD", properties["password"] as String)
    }

    dataBinding {
        enable = true
    }

    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes += "META-INF/**"
            excludes += "win32-x86*/**"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.mockk:mockk:1.13.10")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("org.hamcrest:hamcrest:2.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    androidTestImplementation("org.assertj:assertj-core:3.25.3")
    androidTestImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    androidTestImplementation("de.mannodermaus.junit5:android-test-core:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:1.3.0")

    // glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // room
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // okhttp
    implementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    // gson
    implementation("com.google.code.gson:gson:2.10.0")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")

    // serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
}
