package woowacourse.shopping.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.data.TokenProvider
import woowacourse.shopping.data.service.CartProductApiService
import woowacourse.shopping.data.service.ProductApiService

class RetrofitInstance(tokenProvider: TokenProvider) {
    private val interceptorClient =
        OkHttpClient().newBuilder()
            .addInterceptor(RequestInterceptor(tokenProvider))
            .build()

    val productService: ProductApiService =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(ProductApiService::class.java)

    val cartProductService: CartProductApiService =
        Retrofit
            .Builder()
            .client(interceptorClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(CartProductApiService::class.java)

    companion object {
        private const val BASE_URL =
            "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com"
        private val contentType = "application/json".toMediaType()
    }
}
