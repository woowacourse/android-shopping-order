package woowacourse.shopping.data.remote

import android.util.Base64
import okhttp3.Interceptor

class BasicAuthInterceptor(private val username: String, private val password: String) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        val basicAuth = getBasicAuthHeader(username, password)
        val newRequest =
            originalRequest.newBuilder()
                .header("Authorization", basicAuth)
                .build()
        return chain.proceed(newRequest)
    }

    private fun getBasicAuthHeader(
        username: String,
        password: String,
    ): String {
        val credentials = "$username:$password"
        return "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }
}
