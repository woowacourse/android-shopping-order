package woowacourse.shopping.ui.order.action

sealed class OrderNotifyingActions {
    data object NotifyOrderCompleted : OrderNotifyingActions()

    data object NotifyCanNotOrder : OrderNotifyingActions()
}
