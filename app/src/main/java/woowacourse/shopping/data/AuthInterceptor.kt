package woowacourse.shopping.data

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authToken: String = AuthStorage.authToken ?: return chain.proceed(originalRequest)

        val request: Request =
            originalRequest
                .newBuilder()
                .addHeader(
                    AUTH_HEADER_KEY,
                    authToken,
                ).build()

        val response: Response = chain.proceed(request)
        return response
    }

    companion object {
        private const val AUTH_HEADER_KEY = "Authorization"
    }
}
