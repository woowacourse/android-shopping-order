package woowacourse.shopping.data.source.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.BasicAuthInterceptor
import woowacourse.shopping.data.source.remote.api.CartApiService
import woowacourse.shopping.data.source.remote.api.CouponApiService
import woowacourse.shopping.data.source.remote.api.OrderApiService
import woowacourse.shopping.data.source.remote.api.ProductsApiService

object Client {
    val getProductsApiService: ProductsApiService by lazy {
        retrofit.create(
            ProductsApiService::class.java,
        )
    }

    val getCartApiService: CartApiService by lazy { authRetrofit.create(CartApiService::class.java) }

    val getCouponApiService: CouponApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(CouponApiService::class.java)
    }

    val getOrderApiService: OrderApiService by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(OrderApiService::class.java)
    }

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor())
            .build()

    private val authRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "discountType"
    }

    private const val BASE_URL =
        "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com"
}
