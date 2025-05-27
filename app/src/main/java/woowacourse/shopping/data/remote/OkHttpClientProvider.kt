package woowacourse.shopping.data.remote

import android.util.Base64
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import woowacourse.shopping.data.datasource.local.UserPreference

object OkHttpClientProvider {
    private val id = UserPreference.getUserInfo("id")
    private val password = UserPreference.getUserInfo("password")
    private val credentials = "$id:$password"
    private val basicAuth =
        "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

    fun provideClient(): OkHttpClient {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        return OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
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
