package woowacourse.shopping.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val headerRequest = originalRequest.newBuilder()
            .header(
                "Authorization",
                "Basic Yml4eEBiaXh4LmNvbToxMjM0",
            )
            .build()
        return chain.proceed(headerRequest)
    }
}
