package woowacourse.shopping.domain.model

class Cart(
    cartProducts: List<CartProduct> = emptyList(),
) {
    private val cartProductMap = cartProducts.associateBy { it.product.id }.toMutableMap()

    fun addCartProduct(cartProduct: CartProduct) {
        cartProductMap[cartProduct.product.id] = cartProduct
    }

    fun addAllCartProducts(cartProducts: List<CartProduct>) {
        cartProductMap.putAll(cartProducts.associateBy { it.product.id })
    }

    fun removeCartProductByCartId(cartId: Long) {
        cartProductMap.values.removeIf { it.cartId == cartId }
    }

    fun updateQuantity(
        productId: Long,
        quantity: Int,
    ) {
        val foundCartProduct = cartProductMap[productId] ?: return
        cartProductMap[productId] = foundCartProduct.copy(quantity = quantity)
    }

    fun getQuantity(productId: Long): Int = cartProductMap[productId]?.quantity ?: DEFAULT_QUANTITY

    fun getCartProduct(productId: Long): CartProduct? = cartProductMap[productId]

    fun getCartProducts(): List<CartProduct> = cartProductMap.values.toList()

    companion object {
        private const val DEFAULT_QUANTITY = 0
    }
}
