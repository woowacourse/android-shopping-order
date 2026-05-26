package woowacourse.shopping.ui.setting

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import woowacourse.shopping.domain.repository.NotificationSettingRepository
import woowacourse.shopping.di.ShoppingRepositoryProvider

class SettingViewModel(
    private val notificationSettingRepository: NotificationSettingRepository =
        ShoppingRepositoryProvider.notificationSettingRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            SettingUiState(
                isUnpaidNotificationEnabled = notificationSettingRepository.isUnpaidNotificationEnabled(),
            ),
        )
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    fun setUnpaidNotificationEnabled(isEnabled: Boolean) {
        notificationSettingRepository.setUnpaidNotificationEnabled(isEnabled)
        _uiState.update { currentState ->
            currentState.copy(isUnpaidNotificationEnabled = isEnabled)
        }
    }
}
