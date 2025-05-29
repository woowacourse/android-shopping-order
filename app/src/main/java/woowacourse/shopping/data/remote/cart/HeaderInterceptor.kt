package woowacourse.shopping.data.remote.cart

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(
    private val id: String,
    private val password: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials = "$id:$password"
        val basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        val newRequest = chain.request().newBuilder()
            .header("Authorization", basic)
            .build()

        return chain.proceed(newRequest)
    }
}
