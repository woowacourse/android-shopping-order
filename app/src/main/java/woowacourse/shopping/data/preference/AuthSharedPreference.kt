package woowacourse.shopping.data.preference

import android.content.Context
import androidx.core.content.edit
import woowacourse.shopping.BuildConfig

class AuthSharedPreference(
    val context: Context,
) {
    private val sharedPreference = context.getSharedPreferences(KEY_AUTH_USERNAME, Context.MODE_PRIVATE)

    fun getAuthUsername(): String? = sharedPreference.getString(KEY_AUTH_USERNAME, null) ?: BuildConfig.DEFAULT_USERNAME

    fun putAuthId(username: String) {
        sharedPreference.edit { putString(KEY_AUTH_USERNAME, username) }
    }

    fun getAuthPassword(): String? = sharedPreference.getString(KEY_AUTH_PASSWORD, null) ?: BuildConfig.DEFAULT_PASSWORD

    fun putAuthPassword(password: String) {
        sharedPreference.edit { putString(KEY_AUTH_PASSWORD, password) }
    }

    companion object {
        private const val KEY_AUTH_USERNAME = "AUTH_USERNAME"
        private const val KEY_AUTH_PASSWORD = "AUTH_PASSWORD"
    }
}
