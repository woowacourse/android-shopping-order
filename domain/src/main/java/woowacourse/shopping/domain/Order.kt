package woowacourse.shopping.domain

data class Order(
    val products: List<CartProduct>,
    val originalPrice: Int,
    val usedPoints: Int,
    val finalPrice: Int
)