package woowacourse.shopping.presentation.ui.cart

sealed interface OrderEvent {
    data object CompleteOrder : OrderEvent

    data object MoveToRecommend : OrderEvent

    data object FinishOrder : OrderEvent
}
