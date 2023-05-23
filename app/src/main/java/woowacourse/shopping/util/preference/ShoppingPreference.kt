package woowacourse.shopping.util.preference

import android.content.Context
import android.content.Context.MODE_PRIVATE

class ShoppingPreference(context: Context) {
    private val pref = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)

    fun getToken(): String? = pref.getString(TOKEN_KEY, "")

    fun setToken(newToken: String) = pref.edit().putString(TOKEN_KEY, newToken).apply()

    companion object {
        private const val PREFERENCE_NAME = "Shopping"
        private const val TOKEN_KEY = "token_key"
    }
}
