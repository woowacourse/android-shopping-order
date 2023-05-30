package woowacourse.shopping.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.remote.service.ProductService
import java.util.Base64
import java.util.concurrent.TimeUnit

object RetrofitModule {
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

    private val shoppingOkHttpClient: OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(authorizationInterceptor)
            .build()

    private lateinit var retrofit: Retrofit

    fun setRetrofit(baseURL: String) {
        retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(shoppingOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val productService: ProductService = retrofit.create(ProductService::class.java)
}
