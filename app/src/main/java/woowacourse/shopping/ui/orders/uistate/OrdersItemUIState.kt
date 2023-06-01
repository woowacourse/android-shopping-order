package woowacourse.shopping.ui.orders.uistate

import woowacourse.shopping.domain.Order

data class OrdersItemUIState(
    val id: Long,
    val totalPrice: Int,
    val orderDescription: String
) {
    companion object {
        fun from(order: Order): OrdersItemUIState {
            return OrdersItemUIState(
                id = order.id,
                totalPrice = order.totalPrice,
                orderDescription = order.orderItems.joinToString(
                    separator = ", ",
                    postfix = " 주문하셨습니다."
                ) { "${it.product.name} ${it.quantity}개" }
            )
        }
    }
}
