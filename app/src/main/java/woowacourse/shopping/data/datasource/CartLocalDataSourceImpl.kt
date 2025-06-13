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

    override fun fetchCartProductByProductId(productId: Long): CartProduct = cachedCart.fetchCartProductByProductId(productId)

    override fun fetchCartProductByCartId(cartId: Long): CartProduct = cachedCart.fetchCartIdByCartId(cartId)

    override fun findQuantityByProductId(productId: Long): Int? = cachedCart.fetchQuantityByProductId(productId)

    override fun fetchCartIdByProductId(productId: Long): Long = cachedCart.fetchCartIdByProductId(productId)

    override fun fetchCartProductsByProductIds(productIds: List<Long>): List<CartProduct> =
        cachedCart.fetchCartProductsByProductIds(productIds)

    override fun updateQuantityByProductId(
        productId: Long,
        quantity: Int,
    ) {
        cachedCart.updateQuantityByProductId(productId, quantity)
    }
}
