package woowacourse.shopping.model

data class Order(
    val cartItems: List<CartProduct>,
    val totalPrice: Int,
    val currentPoints: Int,
    val earnedPoints: Int,
    val availablePoints: Int
)
