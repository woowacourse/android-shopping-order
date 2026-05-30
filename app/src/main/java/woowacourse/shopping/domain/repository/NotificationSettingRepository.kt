package woowacourse.shopping.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface NotificationSettingRepository {
    val unpaidNotificationEnabled: StateFlow<Boolean>

    fun isUnpaidNotificationEnabled(): Boolean

    fun setUnpaidNotificationEnabled(isEnabled: Boolean)
}
