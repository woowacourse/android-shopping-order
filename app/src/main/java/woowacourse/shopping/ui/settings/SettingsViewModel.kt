package woowacourse.shopping.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import woowacourse.shopping.data.local.SettingsPreferences
import woowacourse.shopping.di.AppContainer

class SettingsViewModel(
    private val preferences: SettingsPreferences,
    private val hasNotificationPermission: () -> Boolean,
) : ViewModel() {
    private val _isNotificationEnabled =
        MutableStateFlow(
            preferences.isNotificationEnabled(default = hasNotificationPermission()),
        )
    private val _requestPermission = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val _showPermissionDeniedMessage = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    val isNotificationEnabled = _isNotificationEnabled
    val requestPermission = _requestPermission.asSharedFlow()
    val showPermissionDeniedMessage = _showPermissionDeniedMessage.asSharedFlow()

    fun updateSetting(wantsOn: Boolean) {
        when {
            !wantsOn -> updateEnabled(false)
            hasNotificationPermission() -> updateEnabled(true)
            else -> _requestPermission.tryEmit(Unit)
        }
    }

    fun onPermissionResult(granted: Boolean) {
        updateEnabled(granted)
        if (!granted) {
            _showPermissionDeniedMessage.tryEmit(Unit)
        }
    }

    private fun updateEnabled(value: Boolean) {
        _isNotificationEnabled.value = value
        preferences.setNotificationEnabled(value)
    }

    companion object {
        fun provideFactory(
            container: AppContainer,
            hasNotificationPermission: () -> Boolean,
        ) = viewModelFactory {
            initializer {
                SettingsViewModel(
                    container.settingsPreferences,
                    hasNotificationPermission,
                )
            }
        }
    }
}
