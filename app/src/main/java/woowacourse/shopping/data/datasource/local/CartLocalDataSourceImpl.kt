package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem

class CartLocalDataSourceImpl : CartLocalDataSource {
    private var cachedCart: Cart = Cart()

    override fun getCart(): Cart = cachedCart

    override fun saveCart(items: List<CartItem>) {
        cachedCart = Cart(items)
    }

    override fun add(cartItem: CartItem) {
        val productId = cartItem.product.productId
        val newItems = cachedCart.cartMapByProductId + (productId to cartItem)
        cachedCart = Cart(newItems.values.toList())
    }

    override fun delete(productId: Long) {
        val newItems = cachedCart.cartMapByProductId - productId
        cachedCart = Cart(newItems.values.toList())
    }

    override fun find(productId: Long): CartItem? = cachedCart.cartMapByProductId[productId]

    override fun exist(productId: Long): Boolean = cachedCart.cartMapByProductId.containsKey(productId)

    override fun clear() {
        cachedCart = Cart()
    }
}
