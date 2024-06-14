package woowacourse.shopping.data.remote

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.BuildConfig
import java.io.IOException

object ApiClient {
    fun getApiClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(provideOkHttpClient(AppInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response =
            with(chain) {
                val newRequest =
                    request().newBuilder()
                        .addHeader(
                            "Authorization",
                            Credentials.basic(BuildConfig.USER_NAME, BuildConfig.PASSWORD),
                        )
                        .build()
                proceed(newRequest)
            }
    }
}
