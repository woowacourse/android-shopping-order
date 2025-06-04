package woowacourse.shopping.domain.model

data class CartItem(
    val goods: Goods,
    val quantity: Int,
    val id: Int = 0,
    val isSelected: Boolean = false,
) {
    val totalPrice: Int get() = goods.price * quantity
}
