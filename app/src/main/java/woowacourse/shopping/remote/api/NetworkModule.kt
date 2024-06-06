package woowacourse.shopping.remote.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.data.provider.AuthProvider
import woowacourse.shopping.remote.interceptor.AuthorizationInterceptor
import woowacourse.shopping.remote.interceptor.HttpExceptionInterceptor

class NetworkModule(baseUrl: BaseUrl, authProvider: AuthProvider) {
    private val client: OkHttpClient =
        OkHttpClient.Builder().addInterceptor(AuthorizationInterceptor(authProvider = authProvider))
            .addInterceptor(HttpExceptionInterceptor()).build()

    private val retrofit: Retrofit =
        Retrofit.Builder().baseUrl(baseUrl.url)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .client(client).build()

    val productService: ProductService = retrofit.create(ProductService::class.java)

    val cartService: CartService = retrofit.create(CartService::class.java)

    val orderService: OrderService = retrofit.create(OrderService::class.java)

    val couponService: CouponService = retrofit.create(CouponService::class.java)

    companion object {
        private val contentType = "application/json".toMediaType()

        private var instance: NetworkModule? = null

        fun setInstance(
            baseUrl: BaseUrl,
            authProvider: AuthProvider,
        ) {
            instance = NetworkModule(baseUrl, authProvider)
        }

        fun getInstance(): NetworkModule = requireNotNull(instance)
    }
}
