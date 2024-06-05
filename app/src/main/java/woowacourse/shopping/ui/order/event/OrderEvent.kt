package woowacourse.shopping.ui.order.event

sealed class OrderEvent {
    data object CompleteOrder : OrderEvent()
}
