package woowacourse.shopping

import java.time.LocalDateTime

class OrderDetail(
    val orderId: Int,
    val totalPrice: Price,
    val spendPoint: Point,
    val spendPrice: Price,
    val orderDate: LocalDateTime,
    val orderItems: OrderProducts
)
