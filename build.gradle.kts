import java.io.File

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ktlint) apply false
}

// Keep Gradle outputs out of Desktop-synced paths to avoid intermittent file I/O timeouts.
val stableBuildRoot = File(System.getProperty("java.io.tmpdir"), "gradle-build/${rootProject.name}")

layout.buildDirectory.set(stableBuildRoot.resolve("root"))

subprojects {
    layout.buildDirectory.set(stableBuildRoot.resolve(name))
    apply(plugin = rootProject.libs.plugins.ktlint.get().pluginId)
}
