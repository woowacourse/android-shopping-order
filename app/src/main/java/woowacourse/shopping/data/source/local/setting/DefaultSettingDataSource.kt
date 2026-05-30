package woowacourse.shopping.data.source.local.setting

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

class DefaultSettingDataSource(
    context: Context,
) : SettingDataSource {
    private val sharedPreference = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

    override fun isPaymentPendingNotificationEnabled(): Boolean = sharedPreference.getBoolean(PAYMENT_PENDING_NOTIFICATION, false)

    override fun setPaymentPendingNotificationEnabled(enabled: Boolean) {
        sharedPreference.edit(commit = true) {
            putBoolean(PAYMENT_PENDING_NOTIFICATION, enabled)
        }
    }

    override fun hasAskedNotificationPermission(): Boolean = sharedPreference.getBoolean(ASKED_NOTIFICATION_PERMISSION, false)

    override fun markNotificationPermissionAsked() {
        sharedPreference.edit(commit = true) {
            putBoolean(ASKED_NOTIFICATION_PERMISSION, true)
        }
    }

    companion object {
        private const val PREFS_NAME = "settings"
        private const val PAYMENT_PENDING_NOTIFICATION = "payment_pending_notification"
        private const val ASKED_NOTIFICATION_PERMISSION = "asked_notification_permission"
    }
}
