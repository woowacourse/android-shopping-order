package woowacourse.shopping.data

import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.nanoTime()

        val response = chain.proceed(request)

        val endTime = System.nanoTime()

        return response
    }
}
