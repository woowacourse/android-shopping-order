package woowacourse.shopping.model

data class OrderUIModel(
    val id: Long,
    val orderItems: List<OrderItemUIModel>,
    val totalPrice: Int,
    val payPrice: Int,
    val earnedPoints: Int,
    val usedPoints: Int,
    val orderDate: String
)
