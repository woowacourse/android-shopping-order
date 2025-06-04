package woowacourse.shopping.data

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.BuildConfig

class BasicAuthInterceptor(
    private val id: String = BuildConfig.USER_ID,
    private val password: String = BuildConfig.USER_PASSWORD,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val credential = Credentials.basic(id, password)
        val request =
            chain.request()
                .newBuilder()
                .addHeader(HEADER_NAME, credential)
                .build()

        return chain.proceed(request)
    }

    companion object {
        private const val HEADER_NAME = "Authorization"
    }
}
