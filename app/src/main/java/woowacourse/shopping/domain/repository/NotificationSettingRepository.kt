package woowacourse.shopping.domain.repository

interface NotificationSettingRepository {
    fun isUnpaidNotificationEnabled(): Boolean

    fun setUnpaidNotificationEnabled(isEnabled: Boolean)
}
