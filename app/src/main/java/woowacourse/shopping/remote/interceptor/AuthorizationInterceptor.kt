package woowacourse.shopping.remote.interceptor

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials: String = Credentials.basic("junjange", "password")
        val request = from(chain.request(), credentials)
        return chain.proceed(request)
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"

        fun from(
            request: Request,
            credentials: String,
        ): Request =
            request.newBuilder()
                .removeHeader(AUTHORIZATION)
                .addHeader(AUTHORIZATION, credentials)
                .build()
    }
}
