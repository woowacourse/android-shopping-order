package woowacourse.shopping.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.remote.service.CartService
import woowacourse.shopping.data.remote.service.OrderService
import woowacourse.shopping.data.remote.service.ProductService
import kotlin.reflect.KClass

object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL
    private const val TEMP_URL = "http://192.168.2.152:3000"
    private val json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    private val httpClient = OkHttpClient.Builder().build()
    val productService = buildToService(ProductService::class)
    val cartService = buildToService(CartService::class)
    val orderService = buildToService(OrderService::class)

    private fun <T : Any> buildToService(clazz: KClass<T>): T =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType()),
            ).build()
            .create(clazz.java)
}
