package woowacourse.shopping.ui.order.uistate

import woowacourse.shopping.domain.OrderItem

data class OrderItemUIState(
    val quantity: Int,
    val product: ProductUIState
) {
    companion object {
        fun from(orderItem: OrderItem): OrderItemUIState {
            return OrderItemUIState(orderItem.quantity, ProductUIState.from(orderItem.product))
        }
    }
}
