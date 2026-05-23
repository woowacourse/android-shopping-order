package woowacourse.shopping.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.setting.SettingRepository

data class SettingUiState(
    val isLoading: Boolean = false,
    val isPaymentNotificationEnabled: Boolean = false,
)

class SettingViewModel(
    private val settingRepository: SettingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    fun initialLoading() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val isNotificationEnabled = settingRepository.isPaymentNotificationEnabled()
            _uiState.update { it.copy(isLoading = false, isPaymentNotificationEnabled = isNotificationEnabled) }
        }
    }

    fun toggleSetting(isNotificationEnabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            settingRepository.setPaymentNotificationEnabled(isNotificationEnabled)
            _uiState.update { it.copy(isLoading = false, isPaymentNotificationEnabled = isNotificationEnabled) }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as ShoppingApplication
                SettingViewModel(app.settingRepository)
            }
        }
    }
}
