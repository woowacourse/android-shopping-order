package woowacourse.shopping.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import woowacourse.shopping.BuildConfig

object UserPreference {
    private lateinit var prefs: SharedPreferences
    private const val PREFERENCE_NAME = "user_prefs"
    private const val PREF_ID_KEY = "id"
    private const val PREF_PASSWORD_KEY = "password"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        saveUserInfo(PREF_ID_KEY, BuildConfig.ID)
        saveUserInfo(PREF_PASSWORD_KEY, BuildConfig.PASSWORD)
    }

    private fun saveUserInfo(
        key: String,
        value: String,
    ) {
        prefs.edit { putString(key, value) }
    }

    fun getUserInfo(key: String): String? = prefs.getString(key, null)

    fun clearUserInfo() {
        prefs.edit { clear() }
    }
}
