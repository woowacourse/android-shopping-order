package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.local.setting.SettingDataSource
import woowacourse.shopping.domain.repository.SettingRepository

class DefaultSettingRepository(
    private val dataSource: SettingDataSource,
) : SettingRepository {
    override fun isPaymentPendingNotificationEnabled(): Boolean = dataSource.isPaymentPendingNotificationEnabled()

    override fun setPaymentPendingNotificationEnabled(enabled: Boolean) = dataSource.setPaymentPendingNotificationEnabled(enabled)

    override fun hasAskedNotificationPermission(): Boolean = dataSource.hasAskedNotificationPermission()

    override fun markNotificationPermissionAsked() {
        dataSource.markNotificationPermissionAsked()
    }
}
