package woowacourse.shopping.data.model

import java.time.LocalDateTime

typealias DataOrder = Order

class Order(
    val orderId: Int,
    val createdAt: LocalDateTime,
    val orderItems: List<DataOrderItem>,
    val totalPrice: DataPrice,
    val usedPoint: DataPoint,
    val earnedPoint: DataPoint
)
