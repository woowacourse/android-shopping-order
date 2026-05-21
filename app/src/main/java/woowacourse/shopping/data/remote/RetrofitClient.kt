package woowacourse.shopping.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.remote.auth.AuthInterceptor
import woowacourse.shopping.data.remote.auth.BasicAuthEncoder
import woowacourse.shopping.data.remote.auth.MockInterceptor
import woowacourse.shopping.data.remote.service.CartService
import woowacourse.shopping.data.remote.service.OrderService
import woowacourse.shopping.data.remote.service.ProductService

object RetrofitClient {
    private val useMock = BuildConfig.IS_MOCK && android.os.Debug.isDebuggerConnected()
    private val BASE_URL = if (useMock) "http://localhost:8080/" else BuildConfig.BASE_URL

    private val json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    private val encoder = BasicAuthEncoder
    private val httpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(AuthInterceptor { encoder.getHeader() })
            .apply {
                if (useMock) {
                    addInterceptor(MockInterceptor())
                }
            }.build()
    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType()),
            ).build()
    val productService: ProductService by lazy { retrofit.create() }
    val cartService: CartService by lazy { retrofit.create() }
    val orderService: OrderService by lazy { retrofit.create() }
}
