package woowacourse.shopping.presentation.setting.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import woowacourse.shopping.di.AppModule
import woowacourse.shopping.domain.repository.SettingRepository
import woowacourse.shopping.presentation.setting.model.SettingUiState

class SettingViewModel(
    private val settingRepository: SettingRepository = AppModule.settingRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            SettingUiState(
                isPaymentPendingNotificationEnabled = settingRepository.isPaymentPendingNotificationEnabled(),
            ),
        )
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    fun togglePaymentPendingNotification(enabled: Boolean) {
        settingRepository.setPaymentPendingNotificationEnabled(enabled)
        _uiState.update { it.copy(isPaymentPendingNotificationEnabled = enabled) }
    }
}
