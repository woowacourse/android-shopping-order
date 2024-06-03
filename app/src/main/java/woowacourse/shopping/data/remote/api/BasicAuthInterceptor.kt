package woowacourse.shopping.data.remote.api

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor(private val credentialsProvider: CredentialsProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val username = credentialsProvider.getUsername()
        val password = credentialsProvider.getPassword()
        val credentials =
            "Basic " + Base64.encodeToString("$username:$password".toByteArray(), Base64.NO_WRAP)

        val request =
            chain.request().newBuilder()
                .addHeader("Authorization", credentials)
                .build()

        return chain.proceed(request)
    }
}
