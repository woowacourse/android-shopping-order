package woowacourse.shopping.data.repository.settings

import android.content.Context
import androidx.core.content.edit
import woowacourse.shopping.domain.repository.PaymentReminderSettingsRepository

class PaymentReminderSettingsRepositoryImpl(
    context: Context,
) : PaymentReminderSettingsRepository {
    private val sharedPreferences =
        context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE,
        )

    override fun isEnabled(): Boolean = sharedPreferences.getBoolean(KEY_PAYMENT_REMINDER_ENABLED, DEFAULT_PAYMENT_REMINDER_ENABLED)

    override fun setEnabled(enabled: Boolean) {
        sharedPreferences
            .edit {
                putBoolean(KEY_PAYMENT_REMINDER_ENABLED, enabled)
            }
    }

    private companion object {
        private const val PREFERENCES_NAME: String = "payment_reminder_preferences"
        private const val KEY_PAYMENT_REMINDER_ENABLED: String = "payment_reminder_enabled"
        private const val DEFAULT_PAYMENT_REMINDER_ENABLED: Boolean = true
    }
}
