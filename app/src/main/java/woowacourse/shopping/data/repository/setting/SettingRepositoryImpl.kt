package woowacourse.shopping.data.repository.setting

import woowacourse.shopping.data.datasource.setting.SettingDataSource

class SettingRepositoryImpl(
    private val dataSource: SettingDataSource,
) : SettingRepository {
    override suspend fun isPaymentNotificationEnabled(): Boolean = dataSource.isPaymentNotificationEnabled()

    override suspend fun setPaymentNotificationEnabled(enabled: Boolean) {
        dataSource.setPaymentNotificationEnabled(enabled)
    }
}
