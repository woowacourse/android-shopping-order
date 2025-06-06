package woowacourse.shopping.di.provider

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig

object RetrofitClient {
    val instance: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(createJsonConverterFactory())
            .build()
    }

    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor(PrettyJsonLogger())
        loggingInterceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        builder.addInterceptor(loggingInterceptor)
        return builder.build()
    }

    private fun createJsonConverterFactory(): Converter.Factory = Json.asConverterFactory("application/json".toMediaType())

    private class PrettyJsonLogger : HttpLoggingInterceptor.Logger {
        private val json =
            Json {
                prettyPrint = true
                prettyPrintIndent = "\t"
            }

        override fun log(message: String) {
            val trimMessage = message.trim()
            if ((trimMessage.startsWith("{") && trimMessage.endsWith("}")) ||
                (trimMessage.startsWith("[") && trimMessage.endsWith("]"))
            ) {
                try {
                    val jsonElement = json.parseToJsonElement(trimMessage)
                    val prettyJson = json.encodeToString(JsonElement.serializer(), jsonElement)
                    Platform.get().log(prettyJson, Platform.INFO, null)
                } catch (e: Exception) {
                    Platform.get().log(message, Platform.WARN, e)
                }
            } else {
                Platform.get().log(message, Platform.INFO, null)
            }
        }
    }
}
