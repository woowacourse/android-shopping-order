package woowacourse.shopping.data.database

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.BuildConfig

class AppInterceptor : Interceptor {
    private val email = BuildConfig.email
    private val password = BuildConfig.password

    override fun intercept(chain: Interceptor.Chain): Response {
        val headString = createAuthorizationHeaderString(email, password)
        val request =
            chain.request()
                .newBuilder()
                .addHeader(AUTHORIZATION_HEADER, headString)
                .build()
        return chain.proceed(request)
    }

    private fun createAuthorizationHeaderString(
        email: String,
        password: String,
    ): String {
        val authString = "$email:$password"
        val encodedAuthString =
            Base64.encodeToString(authString.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

        return "Basic $encodedAuthString"
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
