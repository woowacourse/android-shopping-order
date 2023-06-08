package woowacourse.shopping.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.WoowaApplication
import woowacourse.shopping.data.local.WoowaSharedPreference

object ServicePool {
    private val authStorage = WoowaSharedPreference(WoowaApplication.applicationContext)
    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(authStorage.userToken))
            .build()
    }
    var server: UrlPool = UrlPool.LOGEON
        private set
    val retrofitService: ShoppingOrderService get() = server.service

    fun init(tag: UrlPool) {
        server = tag
    }

    private fun buildRetrofit(baseUrl: String): ShoppingOrderService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ShoppingOrderService::class.java)
    }

    enum class UrlPool(val service: ShoppingOrderService) {
        SUNGHA(buildRetrofit("http://43.200.181.131:8080")),
        LOGEON(buildRetrofit("http://13.125.169.219:8080")),
    }
}
