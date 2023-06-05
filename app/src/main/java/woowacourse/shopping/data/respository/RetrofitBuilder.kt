package woowacourse.shopping.data.respository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.cart.source.service.CartService
import woowacourse.shopping.data.respository.order.service.OrderService
import woowacourse.shopping.data.respository.point.service.PointService
import woowacourse.shopping.data.respository.product.service.ProductService
import java.util.concurrent.TimeUnit

class RetrofitBuilder private constructor(
    url: Server.Url,
    token: Server.Token
) {
    private val retrofit: Retrofit

    init {
        val client = OkHttpClient.Builder().apply {
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            addInterceptor(createInterceptor(token))
        }.build()

        retrofit = Retrofit.Builder()
            .baseUrl(url.value)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
    }

    private fun createInterceptor(token: Server.Token): Interceptor = Interceptor { chain ->
        with(chain) {
            val modifiedRequest = request().newBuilder()
                .addHeader(AUTHORIZATION, BASIC_USER_TOKEN.format(token.value))
                .build()

            proceed(modifiedRequest)
        }
    }

    fun createProductService(): ProductService {
        return retrofit.create(ProductService::class.java)
    }

    fun createCartService(): CartService {
        return retrofit.create(CartService::class.java)
    }

    fun createOrderService(): OrderService {
        return retrofit.create(OrderService::class.java)
    }

    fun createPointService(): PointService {
        return retrofit.create(PointService::class.java)
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val BASIC_USER_TOKEN = "Basic %s"
        private const val CONNECT_TIMEOUT = 15L
        private const val WRITE_TIMEOUT = 15L
        private const val READ_TIMEOUT = 15L

        private var instance: RetrofitBuilder? = null
        fun getInstance(url: Server.Url, token: Server.Token): RetrofitBuilder {
            synchronized(this) {
                instance?.let { return it }
                return RetrofitBuilder(url, token)
            }
        }
    }
}
