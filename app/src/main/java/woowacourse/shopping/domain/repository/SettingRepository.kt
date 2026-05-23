package woowacourse.shopping.domain.repository

interface SettingRepository {
    fun isPaymentPendingNotificationEnabled(): Boolean

    fun setPaymentPendingNotificationEnabled(enabled: Boolean)

    fun hasAskedNotificationPermission(): Boolean

    fun markNotificationPermissionAsked()
}
