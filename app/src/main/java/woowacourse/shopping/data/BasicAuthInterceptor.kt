package woowacourse.shopping.data

import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.BuildConfig
import java.util.Base64

class BasicAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain.request()
                .newBuilder()
                .addHeader(HEADER_NAME, getBasicCredential())
                .build()

        return chain.proceed(request)
    }

    private fun getBasicCredential(): String {
        val credential = "${BuildConfig.USER_ID}$DELIMITER${BuildConfig.USER_PASSWORD}"
        val encoded = Base64.getEncoder().encodeToString(credential.toByteArray())
        return "$AUTHORIZATION_HEADER$encoded"
    }

    companion object {
        private const val HEADER_NAME = "Authorization"
        private const val AUTHORIZATION_HEADER = "Basic "
        private const val DELIMITER = ":"
    }
}
