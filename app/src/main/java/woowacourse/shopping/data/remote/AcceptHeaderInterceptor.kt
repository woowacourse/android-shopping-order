package woowacourse.shopping.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AcceptHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest =
            request
                .newBuilder()
                .apply {
                    if (request.header(ACCEPT_HEADER_NAME) == null) {
                        header(ACCEPT_HEADER_NAME, DEFAULT_ACCEPT_HEADER_VALUE)
                    }
                }.build()
        return chain.proceed(newRequest)
    }

    private companion object {
        private const val ACCEPT_HEADER_NAME = "Accept"
        private const val DEFAULT_ACCEPT_HEADER_VALUE = "application/json"
    }
}
