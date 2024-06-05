package woowacourse.shopping.domain.model

data class CartItemDomain(
    val cartItemId: Int,
    val quantity: Int,
    val product: ProductItemDomain,
) {
    fun plusQuantity(): CartItemDomain {
        return copy(quantity = quantity + 1)
    }

    fun minusQuantity(): CartItemDomain {
        return copy(quantity = (this.quantity - 1).coerceAtLeast(DEFAULT_QUANTITY))
    }

    fun totalPrice(): Int = product.price * quantity

    companion object {
        const val DEFAULT_QUANTITY = 1
    }
}
