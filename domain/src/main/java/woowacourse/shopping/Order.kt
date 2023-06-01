package woowacourse.shopping

import java.time.LocalDateTime

data class Order(
    val firstProductName: String,
    val totalCount: Int,
    val orderId: Int,
    val imageUrl: String,
    val orderDate: LocalDateTime,
    val spendPrice: Price,
)
