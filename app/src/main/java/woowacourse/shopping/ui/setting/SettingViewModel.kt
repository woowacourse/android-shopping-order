package woowacourse.shopping.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.di.ShoppingRepositoryProvider
import woowacourse.shopping.domain.repository.NotificationSettingRepository

class SettingViewModel(
    private val notificationSettingRepository: NotificationSettingRepository =
        ShoppingRepositoryProvider.notificationSettingRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            SettingUiState(
                isUnpaidNotificationEnabled = notificationSettingRepository.unpaidNotificationEnabled.value,
            ),
        )
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            notificationSettingRepository.unpaidNotificationEnabled.collect { isEnabled ->
                _uiState.update { currentState ->
                    currentState.copy(isUnpaidNotificationEnabled = isEnabled)
                }
            }
        }
    }

    fun setUnpaidNotificationEnabled(isEnabled: Boolean) {
        notificationSettingRepository.setUnpaidNotificationEnabled(isEnabled)
        _uiState.update { currentState ->
            currentState.copy(isUnpaidNotificationEnabled = isEnabled)
        }
    }
}
