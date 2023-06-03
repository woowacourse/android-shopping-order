package woowacourse.shopping.model

data class OrderHistory(
    val id: Long,
    val orderItems: List<OrderHistoryItem>,
    val totalPrice: Int,
    val payPrice: Int,
    val earnedPoints: Int,
    val usedPoints: Int,
    val orderDate: String
)
