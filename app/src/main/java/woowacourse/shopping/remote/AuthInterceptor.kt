package woowacourse.shopping.remote

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(private val user: String, private val password: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val basicAuth = "Basic $user:$password"
        val request: Request = chain.request()
            .newBuilder()
            .header("Authorization", basicAuth)
            .build()
        return chain.proceed(request)
    }
}

