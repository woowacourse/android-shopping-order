package woowacourse.shopping.ui.order.action

sealed interface OrderNotifyingActions {
    data object NotifyCanNotOrder : OrderNotifyingActions

    data object NotifyError : OrderNotifyingActions
}
