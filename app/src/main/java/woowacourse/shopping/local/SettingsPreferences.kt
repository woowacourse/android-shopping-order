package woowacourse.shopping.local

import android.content.Context
import android.content.SharedPreferences

class SettingsPreferences(
    context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isPaymentReminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_PAYMENT_REMINDER, true)
        set(value) {
            prefs.edit().putBoolean(KEY_PAYMENT_REMINDER, value).apply()
        }

    companion object {
        private const val PREFS_NAME = "shopping_settings"
        private const val KEY_PAYMENT_REMINDER = "payment_reminder_enabled"
    }
}
