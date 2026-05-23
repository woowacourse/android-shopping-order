package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.local.setting.SettingDataSource
import woowacourse.shopping.domain.repository.SettingRepository

class DefaultSettingRepository(
    private val dataSource: SettingDataSource,
) : SettingRepository {
    override fun isNotificationEnabled(): Boolean = dataSource.isNotificationEnabled()

    override fun setNotificationEnabled(enabled: Boolean) = dataSource.setNotificationEnabled(enabled)
}
