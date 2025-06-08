package woowacourse.shopping.domain.model

class CartProducts(
    val value: List<CartProduct>,
) {
    val totalPrice: Int = value.sumOf { it.totalPrice }

    val totalQuantity: Int = value.sumOf { it.quantity }

    val ids: List<Int> = value.map { it.id }
}
