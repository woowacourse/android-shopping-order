package woowacourse.shopping.data.server

import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(private val credential: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val headerRequest = originalRequest.newBuilder()
            .header(
                "Authorization",
                credential,
            )
            .build()
        return chain.proceed(headerRequest)
    }
}