package woowacourse.shopping.data.source.local.cart

import woowacourse.shopping.data.model.CachedCartItem

class CartItemsLocalDataSource {
    private var cache: MutableSet<CachedCartItem> = mutableSetOf()

    fun add(
        cartId: Long,
        productId: Long,
        quantity: Int,
    ) {
        val item = CachedCartItem(cartId, productId, quantity)
        cache.add(item)
    }

    fun remove(cartId: Long) {
        cache.removeIf { it.cartId == cartId }
    }

    fun findCachedCartId(productId: Long): Long? {
        return cache.find { it.productId == productId }?.cartId
    }

    fun getCachedCartItem(cachedCartItems: List<CachedCartItem>) {
        cache = cachedCartItems.toMutableSet()
    }

    fun getQuantity(productId: Long): Int {
        return cache.find { it.productId == productId }?.quantity ?: 0
    }
}
