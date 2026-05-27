package woowacourse.shopping.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.notification.PaymentReminderPreference
import woowacourse.shopping.notification.PaymentReminderScheduler

class SettingsViewModel(
    isPaymentReminderEnabled: () -> Boolean,
    private val setPaymentReminderEnabled: (Boolean) -> Unit,
    private val cancelPaymentReminder: () -> Unit,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            SettingsUiState(
                isPaymentReminderEnabled = isPaymentReminderEnabled(),
            ),
        )
    val uiState = _uiState.asStateFlow()

    fun updatePaymentReminderEnabled(isEnabled: Boolean) {
        setPaymentReminderEnabled(isEnabled)

        if (isEnabled.not()) {
            cancelPaymentReminder()
        }

        _uiState.update {
            it.copy(isPaymentReminderEnabled = isEnabled)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application = this[APPLICATION_KEY] as ShoppingApplication
                    val context = application.applicationContext

                    SettingsViewModel(
                        isPaymentReminderEnabled = {
                            PaymentReminderPreference.isEnabled(context)
                        },
                        setPaymentReminderEnabled = { isEnabled ->
                            PaymentReminderPreference.setEnabled(context, isEnabled)
                        },
                        cancelPaymentReminder = {
                            PaymentReminderScheduler.cancel(context)
                        },
                    )
                }
            }
    }
}

data class SettingsUiState(
    val isPaymentReminderEnabled: Boolean = true,
)
