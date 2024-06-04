package woowacourse.shopping.data.remote.api

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.data.remote.api.NetworkManager.ACCEPT_HEADER
import woowacourse.shopping.data.remote.api.NetworkManager.ACCEPT_KEY
import woowacourse.shopping.data.remote.api.NetworkManager.AUTH_KEY

class BasicAuthInterceptor(
    private val username: String,
    private val password: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request().newBuilder()
                .addHeader(ACCEPT_KEY, ACCEPT_HEADER)
                .addHeader(AUTH_KEY, makeCredentials())
                .build()
        return chain.proceed(request)
    }

    private fun makeCredentials(): String {
        return "Basic " + Base64.encodeToString("$username:$password".toByteArray(), Base64.NO_WRAP)
    }
}
