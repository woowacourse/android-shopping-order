package woowacourse.shopping.data.api

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthenticationInterceptor(
    private val username: String,
    private val password: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials = "$username:$password"
        val basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        val request =
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", basic)
                .build()
        return chain.proceed(request)
    }
}