package woowacourse.shopping.remote.service

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.create
import woowacourse.shopping.remote.AuthInterceptor
import java.util.concurrent.TimeUnit

object RetrofitModule {
    private const val BASE_URL = "http://54.180.95.212:8080/"
    private const val user = "songpink"
    private const val password = "password"
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private fun jsonConverterFactory(): Converter.Factory {
        return json.asConverterFactory("application/json".toMediaType())
    }

    private fun loggingInterceptor(): Interceptor = HttpLoggingInterceptor().setLevel(
        HttpLoggingInterceptor.Level.BODY
//        if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor.Level.BODY
//        } else {
//            HttpLoggingInterceptor.Level.NONE
//        }
    )

    private fun httpClient(): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(loggingInterceptor())
        .addInterceptor(AuthInterceptor(user, password))
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS).build()

    private val INSTANCE: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient())
        .addConverterFactory(jsonConverterFactory())
        .build()

    fun retrofit(): Retrofit = INSTANCE
}