package com.example.data.datasource.remote

import com.example.data.BuildConfig
import com.example.data.datasource.remote.service.CartItemService
import com.example.data.datasource.remote.service.CouponService
import com.example.data.datasource.remote.service.OrderService
import com.example.data.datasource.remote.service.ProductService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class RetrofitClient(
    baseUrl: String = BASE_URL,
) {
    private val httpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(
                AuthenticationInterceptor(
                    BuildConfig.USER_ID,
                    BuildConfig.USER_PASSWORD,
                ),
            ).build()

    private val json = Json { ignoreUnknownKeys = true }

    private val retrofitBuilder =
        Retrofit.Builder()
            .client(httpClient)
            .baseUrl(baseUrl)
            .addCallAdapterFactory(DataResponseCallAdapterFactory())
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json; charset=UTF8".toMediaType(),
                ),
            )
            .build()

    val productService: ProductService =
        retrofitBuilder.create(ProductService::class.java)

    val cartItemService: CartItemService =
        retrofitBuilder.create(CartItemService::class.java)

    val orderService: OrderService =
        retrofitBuilder.create(OrderService::class.java)

    val couponService: CouponService =
        retrofitBuilder.create(CouponService::class.java)

    companion object {
        private const val BASE_URL = "http://54.180.95.212:8080"
    }
}
