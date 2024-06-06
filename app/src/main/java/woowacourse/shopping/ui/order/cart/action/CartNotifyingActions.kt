package woowacourse.shopping.ui.order.cart.action

sealed class CartNotifyingActions {
    data object NotifyCartItemDeleted : CartNotifyingActions()
}
