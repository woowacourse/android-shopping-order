package woowacourse.shopping.ui.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import woowacourse.shopping.di.ShoppingRepositoryProvider
import woowacourse.shopping.domain.session.PendingOrderSessionManager

class PendingOrderEntryViewModel(
    private val pendingOrderSessionManager: PendingOrderSessionManager =
        ShoppingRepositoryProvider.pendingOrderSessionManager,
) : ViewModel() {
    private val _pendingOrderEntryAction = MutableStateFlow<PendingOrderEntryAction?>(null)
    val pendingOrderEntryAction: StateFlow<PendingOrderEntryAction?> =
        _pendingOrderEntryAction.asStateFlow()

    private var lastHandledToken: Long = 0L

    fun handlePendingOrderEntryRequest(token: Long) {
        if (token == 0L || token == lastHandledToken) return

        lastHandledToken = token
        _pendingOrderEntryAction.value =
            if (pendingOrderSessionManager.hasActiveSession()) {
                PendingOrderEntryAction.OpenPendingOrder
            } else {
                PendingOrderEntryAction.Ignore
            }
    }

    fun consumePendingOrderEntryAction() {
        _pendingOrderEntryAction.value = null
    }
}

sealed interface PendingOrderEntryAction {
    data object OpenPendingOrder : PendingOrderEntryAction

    data object Ignore : PendingOrderEntryAction
}
