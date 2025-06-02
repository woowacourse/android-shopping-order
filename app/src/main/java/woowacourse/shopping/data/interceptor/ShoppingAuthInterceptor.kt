package woowacourse.shopping.data.interceptor

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response

class ShoppingAuthInterceptor(
    private val username: String,
    private val password: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials = "$username:$password"
        val basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        val newRequest =
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", basic)
                .build()

        return chain.proceed(newRequest)
    }
}
