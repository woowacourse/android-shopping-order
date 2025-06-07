package woowacourse.shopping.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.service.CartProductApiService
import woowacourse.shopping.data.service.CouponApiService
import woowacourse.shopping.data.service.OrderApiService
import woowacourse.shopping.data.service.ProductApiService
import woowacourse.shopping.data.token.TokenProvider

class RetrofitInstance(tokenProvider: TokenProvider) {
    private val interceptorClient =
        OkHttpClient().newBuilder()
            .addInterceptor(AuthorizationInterceptor(tokenProvider))
            .build()

    val productService: ProductApiService =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(ProductApiService::class.java)

    val cartProductService: CartProductApiService =
        Retrofit
            .Builder()
            .client(interceptorClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(CartProductApiService::class.java)

    val couponService: CouponApiService =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(CouponApiService::class.java)

    val orderService: OrderApiService =
        Retrofit
            .Builder()
            .client(interceptorClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(OrderApiService::class.java)

    companion object {
        private val contentType = "application/json".toMediaType()
    }
}
