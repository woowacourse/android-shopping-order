package woowacourse.shopping.model

data class OrderInfo(
    val cartItems: List<CartProduct>,
    val totalPrice: Int,
    val currentPoints: Int,
    val earnedPoints: Int,
    val availablePoints: Int,
)
