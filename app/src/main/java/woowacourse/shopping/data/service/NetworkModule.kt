package woowacourse.shopping.data.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig

object NetworkModule {
    private val client =
        OkHttpClient.Builder()
            .addInterceptor(DefaultInterceptor(BuildConfig.USERNAME, BuildConfig.PASSWORD))
            .build()

    private val tokenClient =
        OkHttpClient.Builder().addInterceptor(TokeInterceptor()).build()

    private val retrofit: Retrofit by lazy {
        retrofitBuilder(client)
    }

    private val tokenRetrofit: Retrofit by lazy {
        retrofitBuilder(tokenClient)
    }

    val productService: ProductService = retrofit.create(ProductService::class.java)

    val cartItemService: CartItemService = tokenRetrofit.create(CartItemService::class.java)

    val orderService: OrderService = tokenRetrofit.create(OrderService::class.java)

    val couponService: CouponService = retrofit.create(CouponService::class.java)

    private fun retrofitBuilder(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .build()
}
