package woowacourse.shopping.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.data.remote.service.CartItemApi
import woowacourse.shopping.data.remote.service.OrderApi
import woowacourse.shopping.data.remote.service.ProductApi

object RetrofitModule {
    private val contentType = "application/json".toMediaType()
    private const val BASE_URL = "http://54.180.95.212:8080/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor.basicAuth)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(Json.asConverterFactory(contentType))
        .client(okHttpClient)
        .build()

    val productApi: ProductApi = retrofit.create(ProductApi::class.java)
    val cartItemsApi: CartItemApi = retrofit.create(CartItemApi::class.java)
    val orderApi: OrderApi = retrofit.create(OrderApi::class.java)
}
