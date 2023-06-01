package woowacourse.shopping.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import woowacourse.shopping.data.remote.service.BasketService
import woowacourse.shopping.data.remote.service.OrderService
import woowacourse.shopping.data.remote.service.ProductService
import woowacourse.shopping.data.remote.service.UserPointInfoService
import java.util.Base64
import java.util.concurrent.TimeUnit

object RetrofitModule {
    private const val AUTHORIZATION_FORMAT = "Basic %s"

    private val encodedUserInfo =
        Base64.getEncoder().encodeToString("a@a.com:1234".toByteArray(Charsets.UTF_8))

    private val authorizationInterceptor: Interceptor =
        Interceptor { chain ->
            with(chain) {
                proceed(
                    request()
                        .newBuilder()
                        .addHeader("Authorization", AUTHORIZATION_FORMAT.format(encodedUserInfo))
                        .build()
                )
            }
        }

    private val shoppingOkHttpClient: OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(authorizationInterceptor)
            .build()

    private lateinit var retrofit: Retrofit

    fun setRetrofit(baseURL: String) {
        retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(shoppingOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val productService: ProductService by lazy { retrofit.create(ProductService::class.java) }
    val basketService: BasketService by lazy { retrofit.create(BasketService::class.java) }
    val userPointInfoService: UserPointInfoService by lazy { retrofit.create(UserPointInfoService::class.java) }
    val orderService: OrderService by lazy { retrofit.create(OrderService::class.java) }
}
