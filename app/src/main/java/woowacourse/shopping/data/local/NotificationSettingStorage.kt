package woowacourse.shopping.data.local

interface NotificationSettingStorage {
    fun setNotificationEnabled(enabled: Boolean)
    fun isNotificationEnabled(): Boolean
}
