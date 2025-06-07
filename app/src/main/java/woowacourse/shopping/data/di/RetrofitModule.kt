package woowacourse.shopping.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.network.BasicAuthentication

class RetrofitModule {
    private val baseUrl = BuildConfig.BASE_URL
    private val contentType = "application/json".toMediaType()

    private val logging =
        HttpLoggingInterceptor().apply {
            level = Level.BODY
        }

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

    private val basicAuthOkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(BasicAuthentication())
            .addInterceptor(logging)
            .build()

    @OptIn(ExperimentalSerializationApi::class)
    val basicAuthInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(basicAuthOkHttpClient)
            .addConverterFactory(
                Json.asConverterFactory(contentType),
            )
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(
                Json.asConverterFactory(contentType),
            )
            .build()
    }
}
