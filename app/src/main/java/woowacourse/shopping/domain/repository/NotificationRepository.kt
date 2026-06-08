package woowacourse.shopping.domain.repository

interface NotificationRepository {
    fun isNotificationEnabled(): Boolean

    fun setNotificationEnabled(enabled: Boolean)
}
