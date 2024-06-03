package woowacourse.shopping.data.service

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
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

    private fun retrofitBuilder(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .build()
}
