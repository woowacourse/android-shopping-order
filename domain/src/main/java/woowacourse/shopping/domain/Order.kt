package woowacourse.shopping.domain

data class Order(
    val id: Int,
    val date: String?,
    val products: List<OrderProduct>,
    val totalPrice: Price,
    val usedPoint: Int,
    val earnedPoint: Int,
)
