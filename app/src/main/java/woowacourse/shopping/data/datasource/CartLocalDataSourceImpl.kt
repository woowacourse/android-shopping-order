package woowacourse.shopping.data.datasource

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartProduct

class CartLocalDataSourceImpl : CartLocalDataSource {
    private val cachedCart = Cart()

    override val cachedCartProducts: List<CartProduct> get() = cachedCart.cachedCartProducts

    override fun addAll(cartProducts: List<CartProduct>) {
        cachedCart.addAll(cartProducts)
    }

    override fun addCartProductToCart(cartProduct: CartProduct) {
        cachedCart.addCartProductToCart(cartProduct)
    }

    override fun deleteCartProductFromCartByCartId(cartId: Long) {
        cachedCart.deleteCartProductFromCartByCartId(cartId)
    }

    override fun findCartProductByProductId(productId: Long): CartProduct? = cachedCart.findCartProductByProductId(productId)

    override fun findCartProductByCartId(cartId: Long): CartProduct = cachedCart.findCartIdByCartId(cartId)

    override fun findQuantityByProductId(productId: Long): Int = cachedCart.findQuantityByProductId(productId)

    override fun findCartIdByProductId(productId: Long): Long = cachedCart.findCartIdByProductId(productId)

    override fun updateQuantityByProductId(
        productId: Long,
        quantity: Int,
    ) {
        cachedCart.updateQuantityByProductId(productId, quantity)
    }
}
