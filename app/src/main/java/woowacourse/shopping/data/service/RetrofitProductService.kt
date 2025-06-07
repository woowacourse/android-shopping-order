package woowacourse.shopping.data.service

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import java.util.Base64

object RetrofitProductService {
    private val logging =
        HttpLoggingInterceptor().apply {
            level = Level.BODY
        }

    private val base64Credentials: String by lazy {
        val username = BuildConfig.USER_ID
        val password = BuildConfig.USER_PASSWORD
        val credentials = "$username:$password"
        Base64.getEncoder().encodeToString(credentials.toByteArray())
    }

    private val authInterceptor =
        Interceptor { chain ->
            val original: Request = chain.request()
            val requestWithAuth: Request =
                original
                    .newBuilder()
                    .header("Authorization", "Basic $base64Credentials")
                    .build()
            chain.proceed(requestWithAuth)
        }

    private val okHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

    val INSTANCE: Retrofit by lazy {
        Retrofit
            .Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
