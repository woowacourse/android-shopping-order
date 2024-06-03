package woowacourse.shopping.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.remote.service.CartItemApi
import woowacourse.shopping.data.remote.service.OrderApi
import woowacourse.shopping.data.remote.service.ProductApi

object RetrofitModule {
    private const val BASE_URL = BuildConfig.BASE_URL
    private const val PRODUCT_BASE_URL = "${BASE_URL}/products/"
    private const val CART_ITEMS_BASE_URL = "${BASE_URL}/cart-items/"
    private const val ORDER_BASE_URL = "${BASE_URL}/orders/"
    private val contentType = "application/json".toMediaType()

    private val basicAuthInterceptor =
        Interceptor { chain ->
            val user = BuildConfig.USER
            val password = BuildConfig.PASSWORD
            val credentials = Credentials.basic(user, password)
            val request =
                chain.request().newBuilder()
                    .header("Authorization", credentials)
                    .build()
            chain.proceed(request)
        }

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(basicAuthInterceptor)
            .build()

    val productApi: ProductApi =
        Retrofit.Builder()
            .baseUrl(PRODUCT_BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .client(okHttpClient)
            .build()
            .create(ProductApi::class.java)

    val cartItemsApi: CartItemApi =
        Retrofit.Builder()
            .baseUrl(CART_ITEMS_BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .client(okHttpClient)
            .build()
            .create(CartItemApi::class.java)

    val orderApi: OrderApi =
        Retrofit.Builder()
            .baseUrl(ORDER_BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .client(okHttpClient)
            .build()
            .create(OrderApi::class.java)
}
