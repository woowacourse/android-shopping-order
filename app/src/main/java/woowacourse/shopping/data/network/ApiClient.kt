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
    fun getApiClient(authenticationRepository: AuthenticationRepository): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(provideOkHttpClient(AppInterceptor(authenticationRepository)))
            .addConverterFactory(
                Json.asConverterFactory("application/json".toMediaType()),
            ).build()

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

        return OkHttpClient.Builder().run {
            addInterceptor(loggingInterceptor)
            addInterceptor(interceptor)
            build()
        }
    }
}
