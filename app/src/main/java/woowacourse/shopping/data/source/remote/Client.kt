package woowacourse.shopping.data.source.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.BasicAuthInterceptor
import woowacourse.shopping.data.source.remote.api.CartApiService
import woowacourse.shopping.data.source.remote.api.CouponApiService
import woowacourse.shopping.data.source.remote.api.OrderApiService
import woowacourse.shopping.data.source.remote.api.ProductsApiService

object Client {
    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor())
            .build()

    private val cartApiService: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val orderApiService: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val productApiService: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val couponApiService: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val getProductRetrofitService: ProductsApiService by lazy {
        productApiService.create(
            ProductsApiService::class.java,
        )
    }
    val getCartRetrofitService: CartApiService by lazy { cartApiService.create(CartApiService::class.java) }
    val getOrderRetrofitService: OrderApiService by lazy { orderApiService.create(OrderApiService::class.java) }
    val getCouponApiService: CouponApiService by lazy { couponApiService.create(CouponApiService::class.java)}

    private const val BASE_URL =
        "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com"
}
