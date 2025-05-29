package woowacourse.shopping.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import woowacourse.shopping.data.authentication.repository.AuthenticationRepository

object ApiClient {
    private const val BASE_URL =
        "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/"

    fun getApiClient(authenticationRepository: AuthenticationRepository): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
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
