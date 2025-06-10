package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.data.handleResult
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem

class CartLocalDataSourceImpl : CartLocalDataSource {
    private var cachedCart: Cart = Cart()

    override fun getCart(): Result<Cart> =
        handleResult {
            cachedCart
        }

    override fun saveCart(items: List<CartItem>): Result<Unit> =
        handleResult {
            cachedCart = Cart(items)
        }

    override fun add(cartItem: CartItem): Result<Unit> =
        handleResult {
            val productId = cartItem.product.productId
            val newItems = cachedCart.cartMapByProductId + (productId to cartItem)
            cachedCart = Cart(newItems.values.toList())
        }

    override fun delete(productId: Long): Result<Unit> =
        handleResult {
            val newItems = cachedCart.cartMapByProductId - productId
            cachedCart = Cart(newItems.values.toList())
        }

    override fun find(productId: Long): Result<CartItem?> =
        handleResult {
            cachedCart.cartMapByProductId[productId]
        }

    override fun exist(productId: Long): Result<Boolean> =
        handleResult {
            cachedCart.cartMapByProductId.containsKey(productId)
        }

    override fun clear() {
        cachedCart = Cart()
    }
}
