package woowacourse.shopping.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.ShoppingApplication.Companion.baseUrl
import woowacourse.shopping.network.service.CartService
import woowacourse.shopping.network.service.ProductService
import kotlin.reflect.KClass

object RetrofitClient {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    private val httpClient = OkHttpClient.Builder().build()
    val productService = buildToService(ProductService::class)
    val cartService = buildToService(CartService::class)

    private fun <T : Any> buildToService(clazz: KClass<T>): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(clazz.java)
    }
}
