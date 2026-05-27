package woowacourse.shopping.notification

import android.content.Context
import androidx.core.content.edit

object PaymentReminderPreference {
    private const val PREFERENCES_NAME = "payment_reminder_settings"
    private const val KEY_ENABLED = "payment_reminder_enabled"
    private const val DEFAULT_ENABLED = true

    fun isEnabled(context: Context): Boolean =
        context
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, DEFAULT_ENABLED)

    fun setEnabled(
        context: Context,
        isEnabled: Boolean,
    ) {
        context
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit {
                putBoolean(KEY_ENABLED, isEnabled)
            }
    }
}
