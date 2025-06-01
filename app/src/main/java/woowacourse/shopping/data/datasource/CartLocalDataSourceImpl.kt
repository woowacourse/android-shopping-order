package woowacourse.shopping.data.datasource

import woowacourse.shopping.domain.model.CartProduct

class CartLocalDataSourceImpl : CartLocalDataSource {
    private val cartProductMap = mutableMapOf<Long, CartProduct>()

    override fun addCartProduct(cartProduct: CartProduct) {
        cartProductMap[cartProduct.product.id] = cartProduct
    }

    override fun addAllCartProducts(cartProducts: List<CartProduct>) {
        cartProductMap.putAll(cartProducts.associateBy { it.product.id })
    }

    override fun removeCartProductByCartId(cartId: Long) {
        cartProductMap.values.removeIf { it.cartId == cartId }
    }

    override fun updateQuantity(
        productId: Long,
        quantity: Int,
    ) {
        val foundCartProduct = cartProductMap[productId] ?: return
        cartProductMap[productId] = foundCartProduct.copy(quantity = quantity)
    }

    override fun getQuantity(productId: Long): Int = cartProductMap[productId]?.quantity ?: DEFAULT_QUANTITY

    override fun getCartProduct(productId: Long): CartProduct? = cartProductMap[productId]

    override fun getCartProducts(): List<CartProduct> = cartProductMap.values.toList()

    companion object {
        private const val DEFAULT_QUANTITY = 0
    }
}
