package woowacourse.shopping.model

data class OrderHistory(
    val id: Int,
    val orderItems: List<OrderProduct>,
    val totalPrice: Int,
    val payPrice: Int,
    val earnedPoints: Int,
    val usedPoints: Int,
    val orderDate: String,
)
