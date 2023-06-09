package woowacourse.shopping.data.order

data class Order(
    val id: Long,
    val totalPrice: Int,
    val orderItems: List<OrderItem>
)
