package woowacourse.shopping.data.network

import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.data.TokenProvider

class RequestInterceptor(private val tokenProvider: TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val authorizationValue = HEADER_VALUE_AUTHORIZATION.format(tokenProvider.getToken())

        builder.addHeader(HEADER_NAME_AUTHORIZATION, authorizationValue)

        return chain.proceed(builder.build())
    }

    companion object {
        private const val HEADER_NAME_AUTHORIZATION = "Authorization"
        private const val HEADER_VALUE_AUTHORIZATION = "Basic %s"
    }
}
