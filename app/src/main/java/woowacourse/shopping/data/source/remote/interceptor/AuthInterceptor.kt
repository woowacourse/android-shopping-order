package woowacourse.shopping.data.source.remote.interceptor

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.data.source.local.auth.AuthDataSource

class AuthInterceptor(
    private val authDataSource: AuthDataSource,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { authDataSource.getToken() }
        val newRequest =
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", "Basic $token")
                .build()
        return chain.proceed(newRequest)
    }
}
