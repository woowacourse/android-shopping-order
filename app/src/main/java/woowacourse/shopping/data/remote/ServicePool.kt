package woowacourse.shopping.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServicePool {
    lateinit var retrofitService: ShoppingOrderService
        private set

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
    }

    init {
        retrofitService = buildRetrofit(UrlPool.LOGEON)
    }

    fun init(tag: UrlPool) {
        retrofitService = buildRetrofit(tag)
    }

    private fun buildRetrofit(tag: UrlPool): ShoppingOrderService {
        return Retrofit.Builder()
            .baseUrl(tag.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ShoppingOrderService::class.java)
    }

    enum class UrlPool(val baseUrl: String) {
        SUNGHA("http://43.200.181.131:8080"),
        LOGEON("http://13.125.169.219:8080"), ;

        companion object {
            private const val TAG = "UrlPool"
            fun find(tag: String): UrlPool {
                return values().find { it.name == tag }
                    ?: throw IllegalArgumentException("$TAG / 잘못된 tag입니다. 현재 tag: $tag")
            }
        }
    }
}
