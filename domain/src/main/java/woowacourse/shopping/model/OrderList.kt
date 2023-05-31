package woowacourse.shopping.model

data class OrderList(
    val cartProducts: List<CartProduct>,
    val totalPrice: Int,
    val currentPoints: Int,
    val earnedPoints: Int,
    val availablePoints: Int
)
