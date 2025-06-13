package woowacourse.shopping.data.util

import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.domain.model.Authorization

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain
                .request()
                .newBuilder()
                .addHeader("accept", "*/*")
                .addHeader("Authorization", "Basic ${Authorization.basicKey}")
                .build()
        return chain.proceed(request)
    }
}
