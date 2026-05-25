package woowacourse.shopping.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.domain.repository.NotificationRepository
import woowacourse.shopping.ui.productList.ProductListViewModel

class SettingViewModel(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    private val _notificationEnabled = MutableStateFlow(false)
    val notificationEnabled = _notificationEnabled.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _notificationEnabled.update { notificationRepository.isNotificationEnabled() }
    }

    fun toggleNotification() {
        val enabled = !notificationEnabled.value
        notificationRepository.setNotificationEnabled(enabled)

        _notificationEnabled.update { enabled }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application = (this[APPLICATION_KEY] as ShoppingApplication)
                    val appContainer = application.appContainer
                    SettingViewModel(
                        appContainer.notificationRepository,
                    )
                }
            }
    }
}
