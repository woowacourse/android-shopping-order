package woowacourse.shopping.data.interceptor

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.preference.AuthSharedPreference

class ShoppingAuthInterceptor(
    private val authSharedPreference: AuthSharedPreference,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials = "${getUsername()}:${getPassword()}"
        val basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        val newRequest =
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", basic)
                .build()

        return chain.proceed(newRequest)
    }

    private fun getUsername(): String =
        authSharedPreference.getAuthUsername()
            ?: BuildConfig.DEFAULT_USERNAME.apply {
                authSharedPreference.putAuthId(this)
            }

    private fun getPassword(): String =
        authSharedPreference.getAuthPassword()
            ?: BuildConfig.DEFAULT_PASSWORD.apply {
                authSharedPreference.putAuthPassword(this)
            }
}
