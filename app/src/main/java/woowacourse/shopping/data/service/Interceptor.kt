package woowacourse.shopping.data.service

import okhttp3.Interceptor
import okhttp3.Response

class Interceptor(
    private val base64: String?,
    private val isAuth: Boolean = false,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()

        val modifiedRequest = when (isAuth) {
            true ->
                originRequest.newBuilder()
                    .addHeader("Authorization", "Basic $base64")
                    .build()
            false ->
                originRequest
        }

        return chain.proceed(modifiedRequest)
    }
}
