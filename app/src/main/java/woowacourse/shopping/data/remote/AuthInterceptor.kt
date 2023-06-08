package woowacourse.shopping.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.data.datasource.local.AuthInfoDataSource

class AuthInterceptor(
    private val authInfoDataSource: AuthInfoDataSource,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        val newRequest = request().newBuilder()
            .addHeader("Authorization", authInfoDataSource.getAuthInfo() ?: NO_TOKEN)
            .build()
        proceed(newRequest)
    }

    companion object {
        private const val NO_TOKEN = ""
    }
}
