package woowacourse.shopping.data.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.network.cart.RetrofitCartService
import woowacourse.shopping.data.network.coupon.RetrofitCouponService
import woowacourse.shopping.data.network.order.OrderService
import woowacourse.shopping.data.network.product.RetrofitProductService

object RetrofitClient {
    private val useMock = BuildConfig.IS_MOCK
    private val BASE_URL = if (useMock) "http://localhost:8080/" else BuildConfig.BASE_URL

    private var authToken: String? = null

    fun setToken(token: String) {
        authToken = token
    }

    private val json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

    private val httpClient =
        OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                if (useMock) {
                    chain.proceed(request)
                } else {
                    val newRequest =
                        request
                            .newBuilder()
                            .apply {
                                authToken?.let { addHeader("Authorization", it) }
                            }.build()
                    chain.proceed(newRequest)
                }
            }.apply {
                if (useMock) {
                    addInterceptor(MockInterceptor())
                }
            }.build()

    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType()),
            ).build()

    val productService: RetrofitProductService by lazy { retrofit.create() }
    val cartService: RetrofitCartService by lazy { retrofit.create() }
    val orderService: OrderService by lazy { retrofit.create() }
    val couponService: RetrofitCouponService by lazy { retrofit.create() }
}
