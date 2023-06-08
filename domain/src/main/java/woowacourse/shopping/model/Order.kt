package woowacourse.shopping.model

import java.time.LocalDateTime

data class Order(
    val orderId: Long,
    val orderDateTime: LocalDateTime,
    val orderProducts: List<OrderProduct>,
    val totalPrice: Price,
)
