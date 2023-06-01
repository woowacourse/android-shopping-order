package woowacourse.shopping.ui.order.uistate

import woowacourse.shopping.domain.Order

data class OrderResultUIState(
    val id: Long,
    val totalPrice: Int,
    val orderItems: List<OrderItemUIState>
) {

    companion object {
        fun from(order: Order): OrderResultUIState {
            return OrderResultUIState(
                order.id,
                order.totalPrice,
                order.orderItems.map(OrderItemUIState::from)
            )
        }
    }
}
