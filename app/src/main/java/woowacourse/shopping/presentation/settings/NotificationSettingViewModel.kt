package woowacourse.shopping.presentation.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import woowacourse.shopping.data.source.local.notification.NotificationSettingDataSource
import woowacourse.shopping.di.AppContainer

class NotificationSettingViewModel(
    private val notificationSettingDataSource: NotificationSettingDataSource = AppContainer.notificationSettingDataSource,
) : ViewModel() {
    private val _isNotificationEnabled = MutableStateFlow(notificationSettingDataSource.isNotificationEnabled())
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    fun changeNotification(enabled: Boolean) {
        notificationSettingDataSource.changeNotification(enabled)
        _isNotificationEnabled.value = notificationSettingDataSource.isNotificationEnabled()
    }
}
