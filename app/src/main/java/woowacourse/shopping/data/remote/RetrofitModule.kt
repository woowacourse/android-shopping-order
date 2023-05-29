package woowacourse.shopping.data.remote

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object RetrofitModule {
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

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(shoppingOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val gson = Gson()
}
