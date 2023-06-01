package woowacourse.shopping

import java.time.LocalDateTime

data class Order(
    val orderId: Int,
    val imageUrl: String,
    val orderDate: LocalDateTime,
    val spendPrice: Price,
)
