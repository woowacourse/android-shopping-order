package woowacourse.shopping.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import woowacourse.shopping.data.service.CartItemService
import woowacourse.shopping.data.service.CouponService
import woowacourse.shopping.data.service.OkHttpClientProvider
import woowacourse.shopping.data.service.ProductService

object NetworkModule {
    private const val BASE_URL =
        "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com"

    private val retrofit: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClientProvider.provideClient())
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val productService: ProductService by lazy {
        retrofit.create(ProductService::class.java)
    }

    val cartItemService: CartItemService by lazy {
        retrofit.create(CartItemService::class.java)
    }

    val couponService: CouponService by lazy {
        retrofit.create(CouponService::class.java)
    }
}
