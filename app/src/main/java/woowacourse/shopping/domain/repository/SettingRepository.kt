package woowacourse.shopping.domain.repository

interface SettingRepository {
    fun isPaymentNotificationEnabled(): Boolean

    fun setPaymentNotificationEnabled(enabled: Boolean)
}
