package woowacourse.shopping.domain

import java.time.LocalDate

data class Order(
    val orderId: Int,
    val createdAt: LocalDate,
    val orderItems: List<OrderItem>,
    val totalPrice: Price,
    val usedPoint: Point,
    val earnedPoint: Point
)
