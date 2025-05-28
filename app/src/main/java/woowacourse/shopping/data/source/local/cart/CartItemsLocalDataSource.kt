package woowacourse.shopping.data.source.local.cart

import woowacourse.shopping.data.model.CachedCartItem

class CartItemsLocalDataSource {
    private val cache: MutableSet<CachedCartItem> = mutableSetOf()

    fun add(
        id: Int,
        quantity: Int,
    ) {
        val item = CachedCartItem(id, quantity)
        cache.add(item)
    }

    fun remove(id: Int) {
        cache.removeIf { it.productId == id }
    }

    fun update(
        id: Int,
        quantity: Int,
    ) {
        val target = cache.find { it.productId == id }
        if (target != null) {
            cache.remove(target)
            cache.add(target.copy(quantity = quantity))
        }
    }

    fun isCached(id: Int): Boolean = cache.any { it.productId == id }
}