package woowacourse.shopping.presentation.ui.cart

sealed interface OrderEvent {
    data object MoveToPayment : OrderEvent

    data object MoveToRecommend : OrderEvent

    data object FinishOrder : OrderEvent
}
