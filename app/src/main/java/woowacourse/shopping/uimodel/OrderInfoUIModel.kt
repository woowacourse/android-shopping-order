package woowacourse.shopping.uimodel

data class OrderInfoUIModel(
    val cartItems: List<CartProductUIModel>,
    val totalPrice: Int,
    val currentPoints: Int,
    val earnedPoints: Int,
    val availablePoints: Int,
)
