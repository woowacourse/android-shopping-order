package woowacourse.shopping.data.alarm

import android.content.Context

class PayReminderPreference(
    private val context: Context,
) {
    private val sharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun isEnabled(): Boolean = sharedPreferences.getBoolean(KEY_IS_ENABLED, true)

    companion object {
        private const val PREFERENCE_NAME = "pay_reminder_preference"
        private const val KEY_IS_ENABLED = "is_enabled"
    }
}
