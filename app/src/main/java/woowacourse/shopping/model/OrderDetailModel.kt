package woowacourse.shopping.model

import java.time.LocalDateTime

class OrderDetailModel(
    val orderId: Int,
    val orderedDateTime: LocalDateTime,
    val products: List<OrderDetailProductModel>,
    val totalPrice: Int,
)
