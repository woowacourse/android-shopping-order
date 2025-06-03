package woowacourse.shopping.presentation.recommend

sealed class OrderEvent {
    object OrderItemSuccess : OrderEvent()
    object OrderItemFailure : OrderEvent()
}