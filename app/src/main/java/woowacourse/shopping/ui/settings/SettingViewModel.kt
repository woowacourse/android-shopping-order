package woowacourse.shopping.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.repository.SettingRepository
import woowacourse.shopping.ui.event.UiEvent

class SettingViewModel(
    private val settingRepository: SettingRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            SettingUiState(
                isPaymentNotificationEnabled =
                    settingRepository.isPaymentNotificationEnabled(),
            ),
        )
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun setPaymentNotificationEnabled(enabled: Boolean) {
        settingRepository.setPaymentNotificationEnabled(enabled)
        _uiState.update { it.copy(isPaymentNotificationEnabled = enabled) }
    }

    fun showNotificationPermissionDeniedMessage() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowMessage("알림 권한이 없어 미결제 알림을 켤 수 없습니다."))
        }
    }
}

class SettingViewModelFactory(
    private val notificationSettingRepository: SettingRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingViewModel(notificationSettingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
