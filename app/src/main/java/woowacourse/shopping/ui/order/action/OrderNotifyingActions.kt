package woowacourse.shopping.ui.order.action

sealed class OrderNotifyingActions {
    data object NotifyCartCompleted: OrderNotifyingActions()
}