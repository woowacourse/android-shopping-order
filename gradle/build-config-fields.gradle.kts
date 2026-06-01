import java.util.Properties

val localProperties =
    Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use(::load)
        }
    }

fun Properties.requireBuildConfigStringProperty(name: String): String {
    val value =
        getProperty(name)
            ?.takeIf { it.isNotBlank() }
            ?: throw GradleException(
                "Missing required local.properties value: $name. " +
                    "Copy local.properties.example to local.properties and fill it in.",
            )

    return value
        .toBuildConfigString()
}

fun String.toBuildConfigString(): String =
    replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .let { "\"$it\"" }

extra["shoppingBuildConfigFields"] =
    mapOf(
        "BASE_URL" to localProperties.requireBuildConfigStringProperty("SHOPPING_BASE_URL"),
        "BASIC_AUTH_USER_NAME" to localProperties.requireBuildConfigStringProperty("SHOPPING_BASIC_AUTH_USER_NAME"),
        "BASIC_AUTH_PASSWORD" to localProperties.requireBuildConfigStringProperty("SHOPPING_BASIC_AUTH_PASSWORD"),
    )
