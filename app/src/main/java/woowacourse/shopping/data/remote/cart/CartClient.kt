package woowacourse.shopping.data.remote.cart

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig

object CartClient {
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .addInterceptor(HeaderInterceptor(BuildConfig.USER_ID, BuildConfig.USER_PASSWORD))
            .addInterceptor(logging)
            .build()
    }

    private val logging =
        HttpLoggingInterceptor().apply {
            level = Level.BODY
        }

    private val retrofit: Retrofit by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }
    private val service: CartService by lazy {
        retrofit.create(CartService::class.java)
    }

    fun getRetrofitService(): CartService = service
}
