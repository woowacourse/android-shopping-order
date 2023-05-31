package woowacourse.shopping.data.cart

import woowacourse.shopping.domain.CartItem

object CartItemLocalCache {

    private val cartItems: MutableMap<Long, CartItem> = mutableMapOf()

    var isActivated: Boolean = false
        private set

    @Synchronized
    fun activate(allCartItems: List<CartItem>) {
        isActivated = true
        allCartItems.forEach {
            this.cartItems[it.id] = it
        }
    }

    @Synchronized
    fun save(cartItem: CartItem) {
        cartItems[cartItem.id] = cartItem
    }

    @Synchronized
    fun findAll(): List<CartItem> {
        return cartItems.values
            .sortedBy { it.id }
    }

    @Synchronized
    fun findAll(limit: Int, offset: Int): List<CartItem> {
        return cartItems.values
            .sortedBy { it.id }
            .slice(offset until cartItems.values.size)
            .take(limit)
    }

    @Synchronized
    fun updateCountById(id: Long, count: Int) {
        val cartItem = cartItems[id] ?: return
        val newCartItem = CartItem(id, cartItem.product, cartItem.addedTime, count)
        cartItems[id] = newCartItem
    }

    @Synchronized
    fun deleteById(id: Long) {
        cartItems.remove(id)
    }

    fun clear() {
        cartItems.clear()
        isActivated = false
    }
}
