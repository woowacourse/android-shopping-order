package woowacourse.shopping.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.cart.service.CartService
import woowacourse.shopping.data.product.service.ProductService

object API {
    private val client: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .addInterceptor(AuthInterceptor())
            .addHttpLoggingInterceptor()
            .build()
    }

    private val retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val productService: ProductService = retrofit.create(ProductService::class.java)
    val cartService: CartService = retrofit.create(CartService::class.java)

    private fun OkHttpClient.Builder.addHttpLoggingInterceptor() =
        addInterceptor(
            HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            },
        )
}
