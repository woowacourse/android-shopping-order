package woowacourse.shopping.presentation.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import woowacourse.shopping.data.source.local.notification.NotificationDataSource
import woowacourse.shopping.di.RepositoryProvider

class NotificationSettingViewModel(
    private val notificationDataSource: NotificationDataSource = RepositoryProvider.notificationDataSource,
) : ViewModel() {
    private val _isNotificationEnabled = MutableStateFlow(notificationDataSource.isNotificationEnabled())
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    fun changeNotification(enabled: Boolean) {
        notificationDataSource.changeNotification(enabled)
        _isNotificationEnabled.value = notificationDataSource.isNotificationEnabled()
    }
}
