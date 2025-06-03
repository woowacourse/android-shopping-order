package woowacourse.shopping.data.remote.product

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig

object ProductClient {
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
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

    private val service: ProductService by lazy {
        retrofit.create(ProductService::class.java)
    }

    fun getRetrofitService(): ProductService = service
}
