package woowacourse.shopping.data.remote

import android.util.Base64
import okhttp3.Interceptor
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.local.preferences.ShoppingPreferencesManager
class BasicAuthInterceptor(
    private val shoppingPreferencesManager: ShoppingPreferencesManager
) : Interceptor {
    private val username: String
        get() = shoppingPreferencesManager.getString(ShoppingPreferencesManager.KEY_USERNAME)
            ?: shoppingPreferencesManager.setString(
                ShoppingPreferencesManager.KEY_USERNAME,
                BuildConfig.USER_NAME,
            )

    private val password: String
        get() = shoppingPreferencesManager.getString(ShoppingPreferencesManager.KEY_PASSWORD)
            ?: shoppingPreferencesManager.setString(
                ShoppingPreferencesManager.KEY_PASSWORD,
                BuildConfig.PASSWORD,
            )

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        val basicAuth = getBasicAuthHeader(username, password)
        val newRequest =
            originalRequest.newBuilder()
                .header("Authorization", basicAuth)
                .build()
        return chain.proceed(newRequest)
    }

    private fun getBasicAuthHeader(
        username: String,
        password: String,
    ): String {
        val credentials = "$username:$password"
        return "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }
}

