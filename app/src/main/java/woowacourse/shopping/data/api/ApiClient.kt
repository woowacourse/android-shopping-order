package woowacourse.shopping.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.service.CartItemService
import woowacourse.shopping.data.service.OrderService
import woowacourse.shopping.data.service.ProductService

object ApiClient {
    private const val USERNAME = BuildConfig.AUTH_USERNAME
    private const val PASSWORD = BuildConfig.AUTH_PASSWORD

    private val client =
        OkHttpClient
            .Builder()
            .addInterceptor(BasicAuthenticationInterceptor(USERNAME, PASSWORD))
            .build()

    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = false
        }

    private val contentType = "application/json".toMediaType()

    private val retrofit: Retrofit =
        Retrofit
            .Builder()
            .baseUrl("http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

    val productService: ProductService = retrofit.create(ProductService::class.java)
    val cartItemService: CartItemService = retrofit.create(CartItemService::class.java)
    val orderService: OrderService = retrofit.create(OrderService::class.java)
}
