package woowacourse.shopping.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServicePool {
    var server: UrlPool = UrlPool.LOGEON
        private set
    lateinit var retrofitService: ShoppingOrderService
        private set
    private lateinit var client: OkHttpClient

    fun init(tag: UrlPool, token: String) {
        client = buildOkHttpClient(token)
        server = tag
        retrofitService = buildRetrofit(tag.baseUrl)
    }

    private fun buildOkHttpClient(token: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token))
            .build()
    }

    private fun buildRetrofit(baseUrl: String): ShoppingOrderService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ShoppingOrderService::class.java)
    }

    enum class UrlPool(val baseUrl: String) {
        SUNGHA("http://43.200.181.131:8080"),
        LOGEON("http://13.125.169.219:8080"),
    }
}
