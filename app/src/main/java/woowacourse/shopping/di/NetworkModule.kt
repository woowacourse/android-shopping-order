package woowacourse.shopping.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.create
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.service.CartItemService
import woowacourse.shopping.data.service.CouponService
import woowacourse.shopping.data.service.OkHttpClientProvider
import woowacourse.shopping.data.service.ProductService

object NetworkModule {
    private const val BASE_URL = BuildConfig.BASE_URL
    private var productService: ProductService? = null
    private var cartItemService: CartItemService? = null
    private var couponService: CouponService? = null

    private fun provideRetrofit(): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClientProvider.provideClient())
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()

    fun provideProductService(): ProductService = productService ?: provideRetrofit().create()

    fun provideCartItemService(): CartItemService = cartItemService ?: provideRetrofit().create()

    fun provideCouponService(): CouponService = couponService ?: provideRetrofit().create()
}
