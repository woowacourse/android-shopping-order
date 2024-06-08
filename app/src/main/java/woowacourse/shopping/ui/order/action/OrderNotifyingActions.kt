package woowacourse.shopping.ui.order.action

sealed interface OrderNotifyingActions {
    data object NotifyOrderCompleted : OrderNotifyingActions

    data object NotifyCanNotOrder : OrderNotifyingActions
}
