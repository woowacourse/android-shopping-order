package woowacourse.shopping.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import woowacourse.shopping.data.cart.service.CartService
import woowacourse.shopping.data.product.service.ProductService

object API {
    private val client: OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            ).build()

    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com")
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()

    val productService: ProductService = retrofit.create(ProductService::class.java)
    val cartService: CartService = retrofit.create(CartService::class.java)
}
