package woowacourse.shopping.data.source.local.setting

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

class DefaultSettingDataSource(
    context: Context,
) : SettingDataSource {
    private val sharedPreference = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

    override fun isNotificationEnabled(): Boolean = sharedPreference.getBoolean(NOTIFICATION, false)

    override fun setNotificationEnabled(enabled: Boolean) {
        sharedPreference.edit(commit = true) {
            putBoolean(NOTIFICATION, enabled)
        }
    }

    companion object {
        private const val PREFS_NAME = "settings"
        private const val NOTIFICATION = "notification"
    }
}
