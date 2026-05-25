package woowacourse.shopping.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.localdata.ShoppingSharedPreferences

data class SettingUiState(
    val isNotification: Boolean = false,
)

sealed class SettingUiEvent {
    object NavigateBack : SettingUiEvent()

    data class ShowToastMessage(
        val message: String,
    ) : SettingUiEvent()
}

class SettingViewModel(
    private val shoppingSharedPreferences: ShoppingSharedPreferences,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SettingUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        _uiState.update {
            it.copy(isNotification = shoppingSharedPreferences.getIsNotification())
        }
    }

    fun updateIsNotification(isNotification: Boolean) {
        viewModelScope.launch {
            shoppingSharedPreferences.saveIsNotification(isNotification)

            _uiState.update {
                it.copy(isNotification = isNotification)
            }

            _uiEvent.emit(
                SettingUiEvent.ShowToastMessage(
                    message = "알림 변경에 성공했습니다.",
                ),
            )
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _uiEvent.emit(SettingUiEvent.NavigateBack)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as ShoppingApplication
                    SettingViewModel(
                        shoppingSharedPreferences = app.appContainer.shoppingSharedPreferences,
                    )
                }
            }
    }
}
