package woowacourse.shopping.data.network

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.data.authentication.repository.AuthenticationRepository
import java.io.IOException

class AppInterceptor(
    authenticationRepository: AuthenticationRepository,
) : Interceptor {
    private val basicAuth: String =
        "Basic " +
                Base64.encodeToString(
                    "${authenticationRepository.id}:${authenticationRepository.password}".toByteArray(),
                    Base64.NO_WRAP,
                )

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response =
        with(chain) {
            val newRequest =
                request()
                    .newBuilder()
                    .addHeader("Authorization", basicAuth)
                    .build()
            proceed(newRequest)
        }
}
