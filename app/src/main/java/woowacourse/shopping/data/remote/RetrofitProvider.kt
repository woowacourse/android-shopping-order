package woowacourse.shopping.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.data.remote.api.CartApi
import woowacourse.shopping.data.remote.api.ProductApi
import java.util.concurrent.TimeUnit
import kotlin.getValue

object RetrofitProvider {
    private const val BASE_URL =
        "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/"
    private val json: Json =
        Json {
            ignoreUnknownKeys = true
        }
    private val contentType = "application/json".toMediaType()

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val okHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    private val retrofit: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
    val productApi: ProductApi by lazy {
        retrofit.create(ProductApi::class.java)
    }

    val cartApi: CartApi by lazy {
        retrofit.create(CartApi::class.java)
    }
}
