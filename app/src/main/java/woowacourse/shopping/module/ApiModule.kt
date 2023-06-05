package woowacourse.shopping.module

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.dataSource.remote.CartProductService
import woowacourse.shopping.data.dataSource.remote.OrderService
import woowacourse.shopping.data.dataSource.remote.ProductService
import woowacourse.shopping.data.preferences.UserStore
import woowacourse.shopping.module.server.ServerInfo
import java.util.concurrent.TimeUnit

class ApiModule private constructor(
    private val userStore: UserStore
) {
    private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor { message ->
            android.util.Log.e(HTTP_LOG_TAG, message)
        }
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private fun createInterceptor(): Interceptor = Interceptor { chain ->
        with(chain) {
            val modifiedRequest = request().newBuilder()
                .addHeader(AUTHORIZATION, BASIC_USER_TOKEN.format(userStore.token))
                .build()

            proceed(modifiedRequest)
        }
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            addInterceptor(httpLoggingInterceptor())
            addInterceptor(createInterceptor())
        }.build()
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ServerInfo.url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()
    }

    fun createCartService(): CartProductService {
        return createRetrofit().create(CartProductService::class.java)
    }

    fun createProductService(): ProductService {
        return createRetrofit().create(ProductService::class.java)
    }

    fun createOrderService(): OrderService {
        return createRetrofit().create(OrderService::class.java)
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val BASIC_USER_TOKEN = "Basic %s"
        private const val CONNECT_TIMEOUT = 15L
        private const val WRITE_TIMEOUT = 15L
        private const val READ_TIMEOUT = 15L

        private const val HTTP_LOG_TAG = "OkHttpLog:"

        private var apiModule: ApiModule? = null

        fun getInstance(userStore: UserStore): ApiModule {
            synchronized(this) {
                apiModule?.let { return it }
                return ApiModule(userStore).apply { apiModule = this }
            }
        }
    }
}
