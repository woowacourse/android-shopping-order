package woowacourse.shopping.data.source.local.cart

import woowacourse.shopping.data.model.CachedCartItem

class CartItemsLocalDataSource {
    private val cacheByProductId: MutableMap<Long, CachedCartItem> = mutableMapOf()

    fun saveCartItems(cachedCartItems: List<CachedCartItem>) {
        cacheByProductId.clear()
        cachedCartItems.forEach {
            cacheByProductId[it.productId] = it
        }
    }

    fun add(
        cartId: Long,
        productId: Long,
        quantity: Int,
    ) {
        val item = CachedCartItem(cartId, productId, quantity)
        cacheByProductId[productId] = item
    }

    fun update(
        productId: Long,
        quantity: Int,
    ) {
        val existingItem = cacheByProductId[productId]
        if (existingItem != null) {
            val updatedItem = existingItem.copy(quantity = quantity)
            cacheByProductId[productId] = updatedItem
        }
    }

    fun remove(productId: Long) {
        cacheByProductId.remove(productId)
    }

    fun getCartId(productId: Long): Long? {
        return cacheByProductId[productId]?.cartId
    }

    fun getQuantity(productId: Long): Int {
        return cacheByProductId[productId]?.quantity ?: 0
    }
}
