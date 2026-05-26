package woowacourse.shopping.data.preference

import android.content.Context
import android.content.SharedPreferences
import woowacourse.shopping.domain.repository.NotificationSettingRepository

private const val PREFERENCE_NAME = "shopping_settings"
private const val KEY_UNPAID_NOTIFICATION_ENABLED = "unpaid_notification_enabled"

class SharedPreferencesNotificationSettingRepository(
    private val sharedPreferences: SharedPreferences,
) : NotificationSettingRepository {
    override fun isUnpaidNotificationEnabled(): Boolean = sharedPreferences.getBoolean(KEY_UNPAID_NOTIFICATION_ENABLED, false)

    override fun setUnpaidNotificationEnabled(isEnabled: Boolean) {
        sharedPreferences
            .edit()
            .putBoolean(KEY_UNPAID_NOTIFICATION_ENABLED, isEnabled)
            .apply()
    }

    companion object {
        fun create(context: Context): SharedPreferencesNotificationSettingRepository =
            SharedPreferencesNotificationSettingRepository(
                sharedPreferences =
                    context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE),
            )
    }
}
