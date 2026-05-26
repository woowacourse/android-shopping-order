package woowacourse.shopping.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import woowacourse.shopping.data.alarm.PayReminderPreference

class SettingViewModel(
    private val payReminderPreference: PayReminderPreference,
) : ViewModel() {
    private val _isNotificationEnabled = MutableStateFlow(payReminderPreference.isEnabled())
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    fun toggleNotification(isEnabled: Boolean) {
        _isNotificationEnabled.value = isEnabled
        payReminderPreference.setEnabled(isEnabled)
    }

    companion object {
        fun provideFactory(payReminderPreference: PayReminderPreference): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SettingViewModel(
                        payReminderPreference = payReminderPreference,
                    )
                }
            }
    }
}
