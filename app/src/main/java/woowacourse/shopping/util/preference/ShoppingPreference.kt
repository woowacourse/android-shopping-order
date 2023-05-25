package woowacourse.shopping.util.preference

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class ShoppingPreference(context: Context) : BasePreference {
    private val pref: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)

    override fun getToken(): String? = pref.getString(TOKEN_KEY, "")

    override fun setToken(newToken: String) = pref.edit().putString(TOKEN_KEY, newToken).apply()

    override fun getBaseUrl(): String? = pref.getString(BASE_URL_KEY, "")

    override fun setBaseUrl(newBaseUrl: String) =
        pref.edit().putString(BASE_URL_KEY, newBaseUrl).apply()

    companion object {
        private const val PREFERENCE_NAME = "Shopping"
        private const val TOKEN_KEY = "token_key"
        private const val BASE_URL_KEY = "base_url"
    }
}
