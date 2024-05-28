package woowacourse.shopping.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import woowacourse.shopping.data.remote.service.CartItemApi
import woowacourse.shopping.data.remote.service.OrderApi
import woowacourse.shopping.data.remote.service.ProductApi

object RetrofitModule {
    private val contentType = "application/json".toMediaType()

    private const val BASE_URL = "http://54.180.95.212:8080"
    private const val PRODUCT_BASE_URL = "${BASE_URL}/products"
    private const val CART_ITEMS_BASE_URL = "${BASE_URL}/cart-items"
    private const val ORDER_BASE_URL = "${BASE_URL}/orders"

    val productApi = Retrofit.Builder()
        .baseUrl(PRODUCT_BASE_URL)
        .addConverterFactory(Json.asConverterFactory(contentType))
        .build()
        .create(ProductApi::class.java)


    val cartItemsApi = Retrofit.Builder()
        .baseUrl(CART_ITEMS_BASE_URL)
        .addConverterFactory(Json.asConverterFactory(contentType))
        .build()
        .create(CartItemApi::class.java)

    val orderApi = Retrofit.Builder()
        .baseUrl(ORDER_BASE_URL)
        .addConverterFactory(Json.asConverterFactory(contentType))
        .build()
        .create(OrderApi::class.java)
}