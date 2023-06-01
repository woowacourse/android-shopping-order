package woowacourse.shopping.ui.order.uistate

import woowacourse.shopping.domain.Order

data class OrderUIState(
    val id: Long,
    val totalPrice: Int,
    val orderItems: List<OrderItemUIState>
) {

    companion object {
        fun from(order: Order): OrderUIState {
            return OrderUIState(
                order.id,
                order.totalPrice,
                order.orderItems.map(OrderItemUIState::from)
            )
        }
    }
}
