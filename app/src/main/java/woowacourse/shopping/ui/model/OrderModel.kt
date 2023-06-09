package woowacourse.shopping.ui.model

data class OrderModel(
    val products: List<CartProductModel>,
    val originalPrice: Int,
    val usedPoints: Int,
    val finalPrice: Int
)