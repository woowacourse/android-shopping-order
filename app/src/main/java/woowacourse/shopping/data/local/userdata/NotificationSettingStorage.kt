package woowacourse.shopping.data.local.userdata

interface NotificationSettingStorage {
    fun setNotificationEnabled(enabled: Boolean)

    fun isNotificationEnabled(): Boolean
}
