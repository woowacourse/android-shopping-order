package woowacourse.shopping.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig

object RetrofitModule {
    private const val BASE_URL = BuildConfig.BASE_URL
    private val contentType = "application/json".toMediaType()

    private val defaultAuthInterceptor =
        Interceptor { chain ->
            val user = BuildConfig.USER
            val password = BuildConfig.PASSWORD
            val credentials = Credentials.basic(user, password)
            val request =
                chain.request().newBuilder()
                    .header("Authorization", credentials)
                    .build()
            chain.proceed(request)
        }

    private val defaultOkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(defaultAuthInterceptor)
            .build()

    val defaultBuild: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .client(defaultOkHttpClient)
            .build()
}
