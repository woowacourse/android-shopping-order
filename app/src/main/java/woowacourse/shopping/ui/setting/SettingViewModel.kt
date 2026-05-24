package woowacourse.shopping.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import woowacourse.shopping.data.localdata.UserDataStore

data class SettingUiState(
    val isNotification: Boolean = false,
)

class SettingViewModel(
    private val dataStore: UserDataStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.isNotification.collect { isNotification ->
                _uiState.update {
                    it.copy(isNotification = isNotification)
                }
            }
        }
    }

    fun updateIsNotification(isNotification: Boolean) {
        viewModelScope.launch {
            dataStore.saveIsNotification(isNotification)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as ShoppingApplication
                    SettingViewModel(
                        dataStore = app.appContainer.userDataStore,
                    )
                }
            }
    }
}
