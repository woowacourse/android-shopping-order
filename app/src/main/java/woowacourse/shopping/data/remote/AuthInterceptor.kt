package woowacourse.shopping.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val token: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val headerRequest = originalRequest.newBuilder()
            .header(
                "Authorization",
                token ?: return chain.proceed(chain.request()),
            )
            .build()
        return chain.proceed(headerRequest)
    }
}
