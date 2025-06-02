package woowacourse.shopping.data.service

import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.logging.HttpLoggingInterceptor
import woowacourse.shopping.BuildConfig

object LoggingInterceptorProvider {
    private val json =
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }

    fun provide(): HttpLoggingInterceptor =
        HttpLoggingInterceptor(
            object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    if (message.startsWith("{") || message.startsWith("[")) {
                        runCatching {
                            val parsed = json.parseToJsonElement(message)
                            Log.i(
                                "PrettyLogger",
                                json.encodeToString(JsonElement.serializer(), parsed),
                            )
                        }.onFailure {
                            Log.i("PrettyLogger", message)
                        }
                        return
                    }
                    Log.i("PrettyLogger", message)
                }
            },
        ).apply {
            if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BODY
        }
}
