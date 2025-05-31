package woowacourse.shopping.data.service

import android.util.Base64
import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.datasource.local.UserPreference

object OkHttpClientProvider {
    private val id = UserPreference.getUserInfo("id")
    private val password = UserPreference.getUserInfo("password")
    private val credentials = "$id:$password"
    private val basicAuth =
        "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

    fun provideClient(): OkHttpClient {
        val json =
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }

        val prettyLogger =
            HttpLoggingInterceptor(
                object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        if (message.startsWith("{") || message.startsWith("[")) {
                            try {
                                val parsed = json.parseToJsonElement(message)
                                Log.d(
                                    "PrettyLogger",
                                    json.encodeToString(JsonElement.serializer(), parsed),
                                )
                            } catch (e: Exception) {
                                Log.d("PrettyLogger", message)
                            }
                        } else {
                            Log.d("PrettyLogger", message)
                        }
                    }
                },
            ).apply {
                if (BuildConfig.DEBUG) level = HttpLoggingInterceptor.Level.BODY
            }

        return OkHttpClient
            .Builder()
            .addInterceptor(prettyLogger)
            .addInterceptor { chain ->
                val request =
                    chain
                        .request()
                        .newBuilder()
                        .addHeader("Authorization", basicAuth)
                        .build()
                chain.proceed(request)
            }.build()
    }
}
