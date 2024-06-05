package woowacourse.shopping.remote.api

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import java.io.IOException

object ApiClient {
    fun getApiClient(
        authorizationUserName: String = BuildConfig.authorization_username,
        authorizationPassword: String = BuildConfig.authorization_password,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.base_url)
            .client(
                provideOkHttpClient(
                    AppInterceptor(
                        authorizationUserName,
                        authorizationPassword,
                    ),
                ),
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }

    class AppInterceptor(
        private val authorizationUserName: String,
        private val authorizationPassword: String,
    ) : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response =
            with(chain) {
                val newRequest =
                    request().newBuilder()
                        .addHeader(
                            "Authorization",
                            Credentials.basic(authorizationUserName, authorizationPassword),
                        )
                        .build()
                proceed(newRequest)
            }
    }
}
