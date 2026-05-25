package woowacourse.shopping.data.repository.preferences

import android.content.Context
import androidx.core.content.edit
import woowacourse.shopping.data.repository.NotificationRepository

class SharedPreferenceNotificationRepository(
    context: Context,
) : NotificationRepository {
    private val preferences =
        context.applicationContext.getSharedPreferences(
            PREFERENCE_NAME,
            Context.MODE_PRIVATE,
        )

    override fun isEnabled(): Boolean =
        preferences.getBoolean(KEY_NOTIFICATION_ENABLED, DEFAULT_ENABLED)

    override fun setEnabled(enabled: Boolean) {
        preferences.edit { putBoolean(KEY_NOTIFICATION_ENABLED, enabled) }
    }

    private companion object {
        const val PREFERENCE_NAME = "settings"
        const val KEY_NOTIFICATION_ENABLED = "notification"
        const val DEFAULT_ENABLED = false
    }
}
