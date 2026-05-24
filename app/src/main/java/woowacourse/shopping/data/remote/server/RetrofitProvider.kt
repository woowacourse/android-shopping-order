package woowacourse.shopping.data.remote.server

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig

class RetrofitProvider(
    private val authHeaderProvider: () -> String?,
) {

    private val baseUrl = BuildConfig.BASE_URL

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val authHeader = authHeaderProvider()

        val requestBuilder = originalRequest.newBuilder()
        if (authHeader != null) {
            requestBuilder.header("Authorization", authHeader)
        }
        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()))
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
}
