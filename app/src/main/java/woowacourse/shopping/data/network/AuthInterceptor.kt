package woowacourse.shopping.data.network

import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.BuildConfig

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest =
            chain
                .request()
                .newBuilder()
                .addHeader(AUTH_HEADER_NAME, AUTHORIZATION_KEY)
                .build()

        return chain.proceed(newRequest)
    }

    companion object {
        private const val AUTH_HEADER_NAME = "Authorization"
        private const val AUTHORIZATION_KEY = "Basic ${BuildConfig.AUTHORIZATION_KEY}"
    }
}
