package woowacourse.shopping.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.authentication.repository.AuthenticationRepository

object ApiClient {
    const val MEDIA_TYPE_JSON = "application/json"

    fun getApiClient(): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(
                Json.asConverterFactory(MEDIA_TYPE_JSON.toMediaType()),
            ).build()

    fun getAuthenticationApiClient(authenticationRepository: AuthenticationRepository): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(provideOkHttpClient(AuthenticationInterceptor(authenticationRepository)))
            .addConverterFactory(
                Json.asConverterFactory(MEDIA_TYPE_JSON.toMediaType()),
            ).build()

    fun provideOkHttpClient(interceptor: AuthenticationInterceptor? = null): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

        return OkHttpClient.Builder().run {
            addInterceptor(loggingInterceptor)
            if (interceptor != null) {
                addInterceptor(interceptor)
            }
            build()
        }
    }
}
