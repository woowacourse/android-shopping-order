package woowacourse.shopping.uimodel

import java.time.LocalDateTime

data class OrderHistoryUIModel(
    val id: Int,
    val orderItems: List<OrderProductUIModel>,
    val totalPrice: Int,
    val payPrice: Int,
    val earnedPoints: Int,
    val usedPoints: Int,
    val orderDate: LocalDateTime,
)
