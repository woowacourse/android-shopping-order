package woowacourse.shopping.model

data class OrderList(
    val products: List<OrderProduct>,
    val totalPrice: Int,
    val points: Int,
    val savedPoints: Int,
    val limitPoints: Int
)
