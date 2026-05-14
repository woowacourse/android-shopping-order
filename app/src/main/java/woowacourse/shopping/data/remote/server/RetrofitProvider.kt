package woowacourse.shopping.data.remote.server

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.Retrofit.*
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.remote.server.service.ProductService
import java.io.IOException

class RetrofitProvider(
    private val authHeaderProvider: () -> String?
) {
    private val BASE_URL = "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/"

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val authHeader = authHeaderProvider()

        android.util.Log.d("AUTH_CHECK", "$authHeader")

        val requestBuilder = originalRequest.newBuilder()
        if(authHeader != null) {
            requestBuilder.header("Authorization", authHeader)
        }
        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(Json {ignoreUnknownKeys = true}.asConverterFactory("application/json".toMediaType()))
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
}

