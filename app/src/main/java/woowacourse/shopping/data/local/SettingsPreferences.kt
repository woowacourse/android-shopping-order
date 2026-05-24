package woowacourse.shopping.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isNotificationEnabled(default: Boolean): Boolean =
        prefs.getBoolean(KEY_NOTIFICATION_ENABLED, default)

    fun setNotificationEnabled(value: Boolean) {
        prefs.edit { putBoolean(KEY_NOTIFICATION_ENABLED, value) }
    }

    companion object {
        private const val PREF_NAME = "settings"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
    }
}