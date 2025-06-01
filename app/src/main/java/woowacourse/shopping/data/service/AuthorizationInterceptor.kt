package woowacourse.shopping.data.service

import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(
    private val basicAuth: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", basicAuth)
                .build()
        return chain.proceed(request)
    }
}
