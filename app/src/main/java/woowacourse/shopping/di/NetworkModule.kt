package woowacourse.shopping.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.remote.CartItemService
import woowacourse.shopping.data.remote.OkHttpClientProvider
import woowacourse.shopping.data.remote.ProductService

object NetworkModule {
    private const val BASE_URL = BuildConfig.BASE_URL
    private var productService: ProductService? = null
    private var cartItemService: CartItemService? = null

    private fun provideRetrofit(): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClientProvider.provideClient())
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()

    fun provideProductService(): ProductService = productService ?: provideRetrofit().create(ProductService::class.java)

    fun provideCartItemService(): CartItemService = cartItemService ?: provideRetrofit().create(CartItemService::class.java)
}
