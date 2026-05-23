package woowacourse.shopping.data.repository.setting

interface SettingRepository {
    suspend fun isPaymentNotificationEnabled(): Boolean

    suspend fun setPaymentNotificationEnabled(enabled: Boolean)
}
