package woowacourse.shopping.data.util

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class RetrofitUtil private constructor(
    baseUrl: String,
    private val userId: String,
) {

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

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    companion object {
        private var INSTANCE: RetrofitUtil? = null

        fun getInstance(baseUrl: String = "", userId: String = ""): RetrofitUtil {
            return INSTANCE ?: RetrofitUtil(baseUrl, userId).also { INSTANCE = it }
        }
    }
}
