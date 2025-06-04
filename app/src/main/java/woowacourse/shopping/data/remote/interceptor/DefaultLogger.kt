package woowacourse.shopping.data.remote.interceptor

import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.logging.HttpLoggingInterceptor

class DefaultLogger(
    private val json: Json =
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        },
) : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        if (message.startsWith("{") || message.startsWith("[")) {
            runCatching {
                val parsed = json.parseToJsonElement(message)
                Log.d(
                    "PrettyLogger",
                    json.encodeToString(JsonElement.serializer(), parsed),
                )
            }.onFailure {
                Log.d("PrettyLogger", message)
            }
            return
        }

        Log.d("PrettyLogger", message)
    }
}
