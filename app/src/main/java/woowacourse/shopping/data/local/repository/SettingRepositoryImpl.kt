package woowacourse.shopping.data.local.repository

import android.content.Context
import woowacourse.shopping.domain.repository.SettingRepository
import androidx.core.content.edit

class SettingRepositoryImpl(
    context: Context,
) : SettingRepository {
    private val sharedPreferences =
        context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)

    override fun isPaymentNotificationEnabled(): Boolean = sharedPreferences.getBoolean(KEY_PAYMENT_NOTIFICATION, false)

    override fun setPaymentNotificationEnabled(enabled: Boolean) {
        sharedPreferences
            .edit {
                putBoolean(KEY_PAYMENT_NOTIFICATION, enabled)
            }
    }

    companion object {
        private const val SETTINGS_NAME = "settings"
        private const val KEY_PAYMENT_NOTIFICATION = "notification"
    }
}
