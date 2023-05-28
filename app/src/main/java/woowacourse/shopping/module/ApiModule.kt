package woowacourse.shopping.module

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.dataSource.remote.cart.CartProductService
import woowacourse.shopping.data.dataSource.remote.product.ProductService
import woowacourse.shopping.user.ServerInfo
import java.util.concurrent.TimeUnit

object ApiModule {
    private const val CONNECT_TIMEOUT = 15L
    private const val WRITE_TIMEOUT = 15L
    private const val READ_TIMEOUT = 15L

    private fun createInterceptor(): Interceptor = Interceptor { chain ->
        with(chain) {
            val modifiedRequest = request().newBuilder()
                .addHeader("Authorization", "Basic ${ServerInfo.token}")
                .build()

            proceed(modifiedRequest)
        }
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
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
}
