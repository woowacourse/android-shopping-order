import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val properties =
    Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { load(it) }
        }
    }

fun asBuildConfigString(rawValue: String): String {
    if (rawValue.startsWith("\"") && rawValue.endsWith("\"")) return rawValue
    val escaped = rawValue.replace("\\", "\\\\").replace("\"", "\\\"")
    return "\"$escaped\""
}

fun readConfig(
    localPropertiesKey: String,
    envKey: String,
    defaultValue: String = "",
): String {
    return properties.getProperty(localPropertiesKey)
        ?: providers.environmentVariable(envKey).orNull
        ?: defaultValue
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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "BASE_URL",
            asBuildConfigString(
                readConfig(
                    "BASE_URL",
                    "",
                ),
            ),
        )

        buildConfigField(
            "String",
            "API_USERNAME",
            asBuildConfigString(readConfig("shoppingApiUsername", "")),
        )
        buildConfigField(
            "String",
            "API_PASSWORD",
            asBuildConfigString(readConfig("shoppingApiPassword", "")),
        )
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "BASE_URL",
                asBuildConfigString(
                    readConfig(
                        "DEBUG_BASE_URL",
                        "",
                    ),
                ),
            )
        }
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
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.datastore.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.runner.junit5)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.lifecycle.runtime.compose)

    // define a BOM and its version
    implementation(platform(libs.okhttp.bom))

    // define any required OkHttp artifacts without version
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.mockwebserver)
    implementation(libs.retrofit)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.kotlinx.serialization.converter)
}
