package woowacourse.shopping.domain.model

data class CartItem(
    val cartItemId: Int,
    val quantity: Int,
    val product: Product,
) {
    val totalPrice: Int
        get() = product.price * quantity

    fun plusQuantity(): CartItem {
        return copy(quantity = quantity + 1)
    }

    fun minusQuantity(): CartItem {
        return copy(quantity = (this.quantity - 1).coerceAtLeast(DEFAULT_QUANTITY))
    }

    companion object {
        const val DEFAULT_QUANTITY = 0
    }
}
