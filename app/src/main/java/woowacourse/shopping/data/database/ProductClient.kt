package woowacourse.shopping.data.database

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.domain.service.RetrofitService

object ProductClient {
    private const val BASE_URL = "http://54.180.95.212:8080"
    val client: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient(AppInterceptor()))
            .build()

    val service = client.create(RetrofitService::class.java)

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }

    class AppInterceptor : Interceptor {
        private val email = "junyoung-won"
        private val password = "password"

        override fun intercept(chain: Interceptor.Chain): Response {
            val headString = createAuthorizationHeaderString(email, password)
            val request =
                chain.request()
                    .newBuilder()
                    .addHeader(AUTHORIZATION_HEADER, headString)
                    .build()
            return chain.proceed(request)
        }

        private fun createAuthorizationHeaderString(
            email: String,
            password: String,
        ): String {
            val authString = "$email:$password"

            val encodedAuthString =
                Base64.encodeToString(authString.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

            return "Basic $encodedAuthString"
        }

        companion object {
            const val AUTHORIZATION_HEADER = "Authorization"
        }
    }
}
