package woowacourse.shopping.ui.order.cart.action

sealed interface CartNotifyingActions {
    data object NotifyCartItemDeleted : CartNotifyingActions

    data object NotifyError : CartNotifyingActions
}
