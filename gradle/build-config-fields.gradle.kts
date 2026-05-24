import java.util.Properties

val localProperties =
    Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use(::load)
        }
    }

fun Properties.buildConfigStringProperty(
    name: String,
    defaultValue: String = "",
): String {
    val value =
        getProperty(name, defaultValue)
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
    return "\"$value\""
}

extra["shoppingBuildConfigFields"] =
    mapOf(
        "BASE_URL" to localProperties.buildConfigStringProperty("SHOPPING_BASE_URL", "http://localhost/"),
        "BASIC_AUTH_USER_NAME" to localProperties.buildConfigStringProperty("SHOPPING_BASIC_AUTH_USER_NAME"),
        "BASIC_AUTH_PASSWORD" to localProperties.buildConfigStringProperty("SHOPPING_BASIC_AUTH_PASSWORD"),
    )
