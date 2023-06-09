package woowacourse.shopping.data.network

import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.utils.UserData

class AuthorizationBoxingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val boxedRequest = chain.request().newBuilder()
            .header("Authorization", "Basic ${UserData.credential}")
            .build()
        return chain.proceed(boxedRequest)
    }
}
