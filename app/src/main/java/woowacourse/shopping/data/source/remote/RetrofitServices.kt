package woowacourse.shopping.data.source.remote

import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.data.source.remote.api.CartService
import woowacourse.shopping.data.source.remote.api.ProductService

class RetrofitServices(
    private val baseUrl: String,
    authToken: String,
) {
    private val json: Json = Json { ignoreUnknownKeys = true }

    val authClient =
        OkHttpClient()
            .newBuilder()
            .addInterceptor(AuthInterceptor(authToken))
            .build()
    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(authClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    val cartService: CartService = retrofit.create(CartService::class.java)
    val productService: ProductService = retrofit.create(ProductService::class.java)
}

class AuthInterceptor(
    private val authToken: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", "Basic $authToken")
                .build()
        return chain.proceed(request)
    }
}
