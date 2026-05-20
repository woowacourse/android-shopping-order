package woowacourse.shopping.data.remote.auth

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authProvider: () -> String,
) : Interceptor {
    private val publicPaths = listOf("products")

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val isPublic = publicPaths.any { original.url.encodedPath.contains(it) }

        val request =
            if (isPublic) {
                original
            } else {
                original.newBuilder().header("Authorization", authProvider()).build()
            }

        return chain.proceed(request)
    }
}
