package woowacourse.shopping.data.source.remote.api

import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class RetrofitServices(
    private val baseUrl: String,
    interceptor: Interceptor,
) {
    private val json: Json = Json { ignoreUnknownKeys = true }

    val authClient =
        OkHttpClient()
            .newBuilder()
            .addInterceptor(interceptor)
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
