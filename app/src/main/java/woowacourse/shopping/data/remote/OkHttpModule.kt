package woowacourse.shopping.data.remote

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.Base64
import java.util.concurrent.TimeUnit

object OkHttpModule {
    var BASE_URL = ""
    private const val AUTHORIZATION_FORMAT = "Basic %s"

    private val encodedUserInfo =
        Base64.getEncoder().encodeToString("a@a.com:1234".toByteArray(Charsets.UTF_8))

    private val authorizationInterceptor: Interceptor =
        Interceptor { chain ->
            with(chain) {
                proceed(
                    request()
                        .newBuilder()
                        .addHeader("Authorization", AUTHORIZATION_FORMAT.format(encodedUserInfo))
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
