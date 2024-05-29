package woowacourse.shopping.remote.interceptor

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import woowacourse.shopping.data.provider.AuthProvider

class AuthorizationInterceptor(
    private val authProvider: AuthProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val name = authProvider.name
        val password = authProvider.password

        val credentials: String = Credentials.basic(name, password)
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
