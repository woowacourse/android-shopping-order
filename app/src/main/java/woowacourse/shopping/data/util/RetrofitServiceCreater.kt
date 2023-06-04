package woowacourse.shopping.data.util

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import woowacourse.shopping.ShoppingApplication.Companion.pref
import woowacourse.shopping.data.service.cart.CartService
import woowacourse.shopping.data.service.order.OrderService
import woowacourse.shopping.data.service.point.PointService
import woowacourse.shopping.data.service.product.ProductService
import woowacourse.shopping.data.util.Header.AUTHORIZATION
import woowacourse.shopping.data.util.Header.Companion.CONTENT_TYPE_JSON

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authRequest = chain.request().newBuilder()
            .addHeader(Header.of(AUTHORIZATION), "Basic ${pref.getToken()}")
            .build()
        return chain.proceed(authRequest)
    }
}

private fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()
}

private val json = Json {
    coerceInputValues = true
    encodeDefaults = true
}

private val jsonConverter = json.asConverterFactory(CONTENT_TYPE_JSON)

private val retrofit = Retrofit.Builder()
    .baseUrl(pref.getBaseUrl() ?: "")
    .client(provideOkHttpClient(AuthInterceptor()))
    .addConverterFactory(jsonConverter)
    .build()

val productService: ProductService = retrofit.create(ProductService::class.java)
val cartService: CartService = retrofit.create(CartService::class.java)
val orderService: OrderService = retrofit.create(OrderService::class.java)
val pointService: PointService = retrofit.create(PointService::class.java)
