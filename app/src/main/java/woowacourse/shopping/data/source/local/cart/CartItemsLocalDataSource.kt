package woowacourse.shopping.data.source.local.cart

import woowacourse.shopping.data.model.CachedCartItem

class CartItemsLocalDataSource {
    private val cache: MutableSet<CachedCartItem> = mutableSetOf()

    fun add(
        cartId: Int,
        productId: Int,
    ) {
        val item = CachedCartItem(cartId, productId)
        cache.add(item)
    }

    fun remove(cartId: Int) {
        cache.removeIf { it.cartId == cartId }
    }

    fun findCachedCartId(productId: Int): Int? {
        return cache.find { it.productId == productId }?.cartId
    }
}
