package woowacourse.shopping.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig

object RetrofitClient {
    val instance: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(createJsonConverterFactory())
            .build()
    }

    private fun createOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(createHttpLoggingInterceptor())
            .addInterceptor(AuthInterceptor())
            .build()

    private fun createJsonConverterFactory(): Converter.Factory = Json.asConverterFactory("application/json".toMediaType())

    private fun createHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val prettyJsonLogger = PrettyJsonLogger()
        val interceptor = HttpLoggingInterceptor(prettyJsonLogger)
        interceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return interceptor
    }
}
