package woowacourse.shopping.data.util

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.util.BANDAL

class RetrofitManager private constructor(
    private val userId: String,
    url: String,
    port: String
) {
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val baseUrl = "$url:$port"

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val headerRequest = request.newBuilder()
            .header("Authorization", "Basic $userId")
            .build()
        chain.proceed(headerRequest)
    }

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    companion object {
        private var instance: RetrofitManager? = null

        fun getInstance(url: String, userId: String = BANDAL, port: String = "8080"): RetrofitManager {
            return instance ?: synchronized(this) {
                instance ?: RetrofitManager(url = url, userId = userId, port = port).also { instance = it }
            }
        }
    }
}
