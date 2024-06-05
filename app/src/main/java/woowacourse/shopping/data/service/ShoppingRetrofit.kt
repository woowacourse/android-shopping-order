package woowacourse.shopping.data.service

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.BuildConfig

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
