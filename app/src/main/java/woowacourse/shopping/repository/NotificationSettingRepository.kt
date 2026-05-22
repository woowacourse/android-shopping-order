package woowacourse.shopping.repository

interface NotificationSettingRepository {
    fun isUnpaidNotificationEnabled(): Boolean

    fun setUnpaidNotificationEnabled(isEnabled: Boolean)
}
