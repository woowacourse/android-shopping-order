package woowacourse.shopping.data.source.local.notification

import android.content.SharedPreferences

class NotificationSettingDataSource(
    private val sharedPreferences: SharedPreferences,
) {
    fun changeNotification(enabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(NOTIFICATION_ENABLED, enabled)
            apply()
        }
    }

    fun isNotificationEnabled(): Boolean = sharedPreferences.getBoolean(NOTIFICATION_ENABLED, false)

    companion object {
        private const val NOTIFICATION_ENABLED = "NOTIFICATION_ENABLED"
    }
}
