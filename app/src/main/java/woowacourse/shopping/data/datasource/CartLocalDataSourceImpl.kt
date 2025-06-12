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

    override fun fetchCartProductByProductId(productId: Long): CartProduct =
        cachedCart.fetchCartProductByProductId(productId) ?: throw IllegalStateException("장바구니에 productId $productId 가 없습니다.")

    override fun fetchCartProductByCartId(cartId: Long): CartProduct = cachedCart.fetchCartIdByCartId(cartId)

    override fun fetchQuantityByProductId(productId: Long): Int = cachedCart.fetchQuantityByProductId(productId)

    override fun fetchCartIdByProductId(productId: Long): Long = cachedCart.fetchCartIdByProductId(productId)

    override fun updateQuantityByProductId(
        productId: Long,
        quantity: Int,
    ) {
        cachedCart.updateQuantityByProductId(productId, quantity)
    }
}
