package woowacourse.shopping.data.local

import android.content.Context
import androidx.core.content.edit

class NotificationSettingStorageImpl(context: Context) : NotificationSettingStorage {

    private val appContext = context.applicationContext
    private val sharedPreferences = appContext.getSharedPreferences(
        "notification_settings",
        Context.MODE_PRIVATE
    )

    override fun setNotificationEnabled(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_NOTIFICATION_ENABLE, enabled)
        }
    }

    override fun isNotificationEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLE, true)
    }

    companion object {
        private const val KEY_NOTIFICATION_ENABLE = "key_notification_enabled"
    }
}