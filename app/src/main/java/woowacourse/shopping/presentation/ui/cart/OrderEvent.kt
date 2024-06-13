package woowacourse.shopping.presentation.ui.cart

sealed interface OrderEvent {
    data class MoveToPayment(val selectedCartIds: List<Long>, val totalPrice: Long) : OrderEvent

    data object MoveToRecommend : OrderEvent
}
