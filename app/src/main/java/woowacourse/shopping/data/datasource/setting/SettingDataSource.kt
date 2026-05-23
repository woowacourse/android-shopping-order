package woowacourse.shopping.data.datasource.setting

interface SettingDataSource {
    suspend fun isPaymentNotificationEnabled(): Boolean

    suspend fun setPaymentNotificationEnabled(enabled: Boolean)
}
