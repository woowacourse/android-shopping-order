package woowacourse.shopping.storage.sharedpreferences

import android.content.Context
import android.content.SharedPreferences

class NotificationPreferenceRepository(
    context: Context,
) {
    // 로컬에 알림 설정값을 저장하기 위한 SharedPreferences 생성, 기본값은 true
    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(
            "notification_settings",
            Context.MODE_PRIVATE
        )

    fun isNotificationEnabled(): Boolean =
        sharedPreferences.getBoolean("is_notification_enabled", true)

    fun setNotificationEnabled(enabled: Boolean) {
        sharedPreferences
            .edit()
            .putBoolean("is_notification_enabled", enabled)
            .apply()
    }
}
