package woowacourse.shopping.data.source.local.setting

interface SettingDataSource {
    fun isPaymentPendingNotificationEnabled(): Boolean

    fun setPaymentPendingNotificationEnabled(enabled: Boolean)

    fun hasAskedNotificationPermission(): Boolean

    fun markNotificationPermissionAsked()
}
