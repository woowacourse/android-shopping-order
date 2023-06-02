package woowacourse.shopping.model

data class Order(
    val id: Long,
    val orderItems: List<OrderItem>,
    val totalPrice: Int,
    val payPrice: Int,
    val earnedPoints: Int,
    val usedPoints: Int,
    val orderDate: String
)
