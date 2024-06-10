package woowacourse.shopping.remote.interceptor

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthorizationInterceptor(
    private val authorizationUserName: String,
    private val authorizationPassword: String,
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response =
        with(chain) {
            val newRequest =
                request().newBuilder()
                    .addHeader(
                        "Authorization",
                        Credentials.basic(authorizationUserName, authorizationPassword),
                    )
                    .build()
            proceed(newRequest)
        }
}
