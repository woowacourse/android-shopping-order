package woowacourse.shopping.ui.setting

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import woowacourse.shopping.domain.repository.NotificationRepository

class SettingViewModel(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    private val _notificationEnabled = MutableStateFlow(false)
    val notificationEnabled = _notificationEnabled.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _notificationEnabled.update { notificationRepository.isNotificationEnabled() }
    }

    fun toggleNotification() {
        val enabled = !notificationEnabled.value
        notificationRepository.setNotificationEnabled(enabled)

        _notificationEnabled.update { enabled }
    }
}
