package woowacourse.shopping.data.remote

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import java.util.concurrent.TimeUnit

object OkHttpModule {
    var BASE_URL = "http://15.164.103.138:8080"

    private val encodedUserInfo =
        Base64.getEncoder().encodeToString("a@a.com:1234".toByteArray(Charsets.UTF_8))

    private val authorizationInterceptor: Interceptor =
        Interceptor { chain ->
            with(chain) {
                proceed(
                    request()
                        .newBuilder()
                        .addHeader("Authorization", encodedUserInfo)
                        .build()
                )
            }
        }

    val shoppingOkHttpClient: OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(authorizationInterceptor)
            .build()

    val gson = Gson()
}
