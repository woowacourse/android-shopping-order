package woowacourse.shopping.data.respository

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.preference.UserPreference
import woowacourse.shopping.data.respository.cart.source.service.CartService
import woowacourse.shopping.data.respository.order.service.OrderService
import woowacourse.shopping.data.respository.product.service.ProductService
import java.util.concurrent.TimeUnit

class RetrofitBuilder private constructor(
    context: Context,
    url: Server.Url,
) {
    private val retrofit: Retrofit
    private val user: UserPreference = UserPreference.getInstance(context)

    init {
        val token = user.token

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

    private fun createInterceptor(token: String): Interceptor = Interceptor { chain ->
        with(chain) {
            val modifiedRequest = request().newBuilder()
                .addHeader(AUTHORIZATION, BASIC_USER_TOKEN.format(token))
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

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val BASIC_USER_TOKEN = "Basic %s"
        private const val CONNECT_TIMEOUT = 15L
        private const val WRITE_TIMEOUT = 15L
        private const val READ_TIMEOUT = 15L

        @Volatile
        private var instance: RetrofitBuilder? = null
        fun getInstance(context: Context, url: Server.Url): RetrofitBuilder {
            synchronized(this) {
                instance?.let { return it }
                return RetrofitBuilder(context, url)
            }
        }
    }
}
