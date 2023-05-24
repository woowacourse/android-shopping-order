package woowacourse.shopping.data.common

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) : SharedPreferencesDb {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }

    override fun setString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }

    companion object {
        private const val PREF_NAME = "prefs"
    }
}
