package woowacourse.shopping.data.remote.api

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.remote.service.CartItemService
import woowacourse.shopping.data.remote.service.OrderService
import woowacourse.shopping.data.remote.service.ProductService

object ShoppingRetrofit {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .client(ShoppingOkHttpClient.INSTANCE)
            .build()
    }

    val productService: ProductService = retrofit.create(ProductService::class.java)

    val cartItemService: CartItemService = retrofit.create(CartItemService::class.java)

    val orderService: OrderService = retrofit.create(OrderService::class.java)
}
