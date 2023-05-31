package woowacourse.shopping.domain.model

data class CartProduct(
    val product: Product,
    val cartItem: CartItem,
    val isChecked: Boolean = true,
) {
    init {
        require(cartItem.quantity >= 0) { "수량은 음수일 수 없습니다. 현재 수량: ${cartItem.quantity}" }
    }

    fun increase(): CartProduct = CartProduct(product, CartItem(cartItem.id, cartItem.quantity + 1))

    fun decrease(): CartProduct {
        val decreasedQuantity = if (cartItem.quantity < 1) 0 else cartItem.quantity - 1
        return CartProduct(product, CartItem(cartItem.id, decreasedQuantity))
    }
}
