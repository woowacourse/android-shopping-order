package woowacourse.shopping.network.auth

import okhttp3.Interceptor
import okhttp3.Response


class BasicAuthInterceptor(
    private val headerProvider: () -> String?,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val header = headerProvider()

        if (header.isNullOrBlank() || originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        val authenticatedRequest =
            originalRequest.newBuilder()
                .header("Authorization", header)
                .build()

        return chain.proceed(authenticatedRequest)
    }
}
