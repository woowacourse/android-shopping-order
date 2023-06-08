package woowacourse.shopping.domain

import java.time.LocalDateTime

data class Order(
    val orderId: Int,
    val createdAt: LocalDateTime,
    val orderItems: List<OrderItem>,
    val totalPrice: Price,
    val usedPoint: Point,
    val earnedPoint: Point
)
