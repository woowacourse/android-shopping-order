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

    fun deleteCartProductFromCartByCartId(cartId: Long) {
        cachedCartProducts.values.removeIf { it.cartId == cartId }
    }

    fun findCartProductByProductId(productId: Long): CartProduct? = cachedCartProducts[productId]

    fun findQuantityByProductId(productId: Long): Int = cachedCartProducts[productId]?.quantity ?: DEFAULT_QUANTITY

    fun findCartIdByProductId(productId: Long): Long =
        requireNotNull(cachedCartProducts[productId]?.cartId) { NOT_FOUND_CART_ID_ERROR_MESSAGE }

    fun updateQuantityByProductId(
        productId: Long,
        quantity: Int,
    ) {
        val foundCartProduct = cachedCartProducts[productId] ?: return
        cachedCartProducts[productId] = foundCartProduct.copy(quantity = quantity)
    }

    companion object {
        private const val DEFAULT_QUANTITY = 0
        private const val NOT_FOUND_CART_ID_ERROR_MESSAGE = "해당 상품의 카트 ID를 찾을 수 없습니다."
    }
}
