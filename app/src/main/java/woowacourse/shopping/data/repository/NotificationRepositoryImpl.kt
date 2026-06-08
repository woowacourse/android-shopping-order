package woowacourse.shopping.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import woowacourse.shopping.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : NotificationRepository {
    override fun isNotificationEnabled(): Boolean = sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, false)

    override fun setNotificationEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_NOTIFICATION_ENABLED, enabled) }
    }

    companion object {
        private const val KEY_NOTIFICATION_ENABLED = "NOTIFICATION_ENABLED"
    }
}
