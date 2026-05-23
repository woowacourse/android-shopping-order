package woowacourse.shopping.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import woowacourse.shopping.local.SettingsPreferences

class SettingsViewModel(
    private val settingsPreferences: SettingsPreferences,
) : ViewModel() {
    private val _isPaymentReminderEnabled =
        MutableStateFlow(settingsPreferences.isPaymentReminderEnabled)
    val isPaymentReminderEnabled: StateFlow<Boolean> = _isPaymentReminderEnabled.asStateFlow()

    fun togglePaymentReminder(enabled: Boolean) {
        settingsPreferences.isPaymentReminderEnabled = enabled
        _isPaymentReminderEnabled.update { enabled }
    }

    companion object {
        fun factory(settingsPreferences: SettingsPreferences) =
            viewModelFactory {
                initializer {
                    SettingsViewModel(settingsPreferences)
                }
            }
    }
}
