package woowacourse.shopping.data.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor

class PrettyJsonLogger : HttpLoggingInterceptor.Logger {
    private val json =
        Json {
            prettyPrint = true
            prettyPrintIndent = "\t"
        }

    override fun log(message: String) {
        val trimmed = message.trim()
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) ||
            (trimmed.startsWith("[") && trimmed.endsWith("]"))
        ) {
            try {
                val jsonElement = json.parseToJsonElement(trimmed)
                val pretty = json.encodeToString(JsonElement.serializer(), jsonElement)
                Platform.get().log(pretty, Platform.INFO, null)
            } catch (e: Exception) {
                Platform.get().log(message, Platform.WARN, e)
            }
        } else {
            Platform.get().log(message, Platform.INFO, null)
        }
    }
}
