package woowacourse.shopping.data

import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.di.AuthStorageModule

class AuthInterceptor(
    private val authStorage: AuthStorage = AuthStorageModule.authStorage,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestWithAuth =
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", authStorage.authorization)
                .build()
        return chain.proceed(requestWithAuth)
    }
}
