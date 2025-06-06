package woowacourse.shopping.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.remote.cart.CartService
import woowacourse.shopping.data.remote.cart.HeaderInterceptor
import woowacourse.shopping.data.remote.coupon.CouponService
import woowacourse.shopping.data.remote.product.ProductService

object NetworkClient {
    private val contentType = "application/json".toMediaType()

    private val okHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(HeaderInterceptor(BuildConfig.USER_ID, BuildConfig.USER_PASSWORD))
            .build()

    private val retrofitService: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.Default.asConverterFactory(contentType))
            .build()
    }

    fun getCartService(): CartService = retrofitService.create(CartService::class.java)

    fun getProductService(): ProductService = retrofitService.create(ProductService::class.java)

    fun getCouponService(): CouponService = retrofitService.create(CouponService::class.java)
}
