package woowacourse.shopping.data.remote.interceptor

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.data.datasource.local.UserPreference

class AuthorizationInterceptor : Interceptor {
    private val id = UserPreference.getUserInfo("id")
    private val password = UserPreference.getUserInfo("password")
    private val credentials = "$id:$password"
    private val basicAuth =
        "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request =
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", basicAuth)
                .build()
        return chain.proceed(request)
    }
}
