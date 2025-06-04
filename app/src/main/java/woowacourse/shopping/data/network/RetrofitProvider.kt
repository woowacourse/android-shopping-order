package woowacourse.shopping.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.network.service.CartService
import woowacourse.shopping.data.network.service.ProductService

object RetrofitProvider {
    private const val BASE_URL = BuildConfig.BASE_URL
    private val contentType = "application/json".toMediaType()

    private val logging =
        HttpLoggingInterceptor().apply {
            level = Level.BODY
        }

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

    private val basicAuthOkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(BasicAuthentication())
            .addInterceptor(logging)
            .build()

    val json = Json {
        ignoreUnknownKeys = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val BASIC_AUTH_INSTANCE: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(basicAuthOkHttpClient)
            .addConverterFactory(
                json.asConverterFactory(contentType),
            )
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val INSTANCE: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory(contentType),
            )
            .build()
    }

    val productService: ProductService = INSTANCE.create(ProductService::class.java)

    val cartService: CartService = BASIC_AUTH_INSTANCE.create(CartService::class.java)
}
