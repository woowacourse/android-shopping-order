package woowacourse.shopping.domain.model

import java.time.LocalDateTime

data class Orders(
    val orderItems: List<OrderItem>,
    val totalPrice: Int = orderItems.sumOf { it.product.price * it.quantity },
    val orderDateTime: LocalDateTime,
    val deliveryFee: Int = DEFAULT_DELIVERY_FEE,
) {
    companion object {
        private const val DEFAULT_DELIVERY_FEE = 3_000

        val DEFAULT =
            Orders(
                orderItems = listOf(),
                totalPrice = 0,
                orderDateTime = LocalDateTime.now(),
                deliveryFee = DEFAULT_DELIVERY_FEE,
            )
    }
}

data class OrderItem(
    val cartItemId: Long,
    val quantity: Int,
    val product: Product,
)
