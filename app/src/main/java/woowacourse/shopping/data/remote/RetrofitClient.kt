package woowacourse.shopping.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://54.180.95.212:8080"
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .addInterceptor(
                AuthenticationInterceptor(
                    "kimhm0728",
                    "password",
                ),
            ).build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResultCallAdapter.Factory())
            .build()
    }

    val productApi: ProductService by lazy { retrofit.create(ProductService::class.java) }

    val cartItemApi: CartItemService by lazy { retrofit.create(CartItemService::class.java) }

    val couponApi: CouponService by lazy { retrofit.create(CouponService::class.java) }

    val orderApi: OrderService by lazy { retrofit.create(OrderService::class.java) }
}
