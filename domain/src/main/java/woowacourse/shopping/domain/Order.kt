package woowacourse.shopping.domain

class Order(
    val id: Long,
    val totalPrice: Int,
    val orderItems: List<OrderItem>
) {

    override fun equals(other: Any?): Boolean = if (other is Order) id == other.id else false

    override fun hashCode(): Int = id.hashCode()
}
