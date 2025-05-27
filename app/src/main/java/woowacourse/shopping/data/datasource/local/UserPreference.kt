package woowacourse.shopping.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object UserPreference {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    fun saveUserInfo(
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
