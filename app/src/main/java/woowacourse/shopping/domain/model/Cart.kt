package woowacourse.shopping.domain.model

class Cart(
    cartProducts: List<CartProduct> = emptyList(),
) {
    private val cachedCartProducts = cartProducts.associateBy { it.product.id }.toMutableMap()

    fun addAll(cartProducts: List<CartProduct>) {
        cachedCartProducts.putAll(cartProducts.associateBy { it.product.id })
    }

    fun addCartProductToCart(cartProduct: CartProduct) {
        cachedCartProducts[cartProduct.product.id] = cartProduct
    }

    fun findCartProductByProductId(productId: Long): CartProduct? = cachedCartProducts[productId]

    fun findQuantityByProductId(productId: Long): Int = cachedCartProducts[productId]?.quantity ?: 0

    fun findCartIdByProductId(productId: Long): Long = cachedCartProducts[productId]?.cartId ?: 0

    fun updateQuantityByProductId(
        productId: Long,
        quantity: Int,
    ) {
        val foundCartProduct = cachedCartProducts[productId] ?: return
        cachedCartProducts[productId] = foundCartProduct.copy(quantity = quantity)
    }
}
