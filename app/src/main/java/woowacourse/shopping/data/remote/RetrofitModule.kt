package woowacourse.shopping.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import java.io.IOException

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

    private val defaultExceptionInterceptor =
        Interceptor { chain ->
            val response = chain.proceed(chain.request())
            if(!response.isSuccessful) {
                when (response.code) {
                    400 -> throw IllegalArgumentException("Bad Request (400)")
                    401 -> throw SecurityException("Unauthorized (401)")
                    403 -> throw SecurityException("Forbidden (403)")
                    404 -> throw NoSuchElementException("Not Found (404)")
                    500 -> throw IOException("Internal Server Error (500)")
                    else -> throw Exception("Unknown HTTP error code: $response.code")
                }
            }
            response
        }



    private val defaultOkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(defaultAuthInterceptor)
            .addInterceptor(defaultExceptionInterceptor)
            .build()

    val defaultBuild: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .client(defaultOkHttpClient)
            .build()
}
