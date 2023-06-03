package woowacourse.shopping.model

data class OrderUIModel(
    val cartProducts: List<CartProductUIModel>,
    val totalPrice: Int,
    val currentPoints: Int,
    val earnedPoints: Int,
    val availablePoints: Int
)
