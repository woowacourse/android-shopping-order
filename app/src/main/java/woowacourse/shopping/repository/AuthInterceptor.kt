package woowacourse.shopping.repository

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authHeaderProvider: AuthHeaderProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // id/password 가져오기
        val authorizationHeader =
            runBlocking {
                authHeaderProvider.getAuthorizationHeader()
            }

        val newRequest =
            chain.request()
                .newBuilder()
                .apply{
                    if(authorizationHeader != null){
                        addHeader("Authorization",authorizationHeader)
                    }
                }.build()
        return chain.proceed(newRequest)
    }
}
