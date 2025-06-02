package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem

class FakeCartRepository : CartRepository {
    private val cartItems = mutableListOf<CartItem>()

    override fun loadPageOfCartItems(
        pageSize: Int,
        offset: Int,
        callback: (List<CartItem>, Boolean) -> Unit,
    ) {
        callback(cartItems, false)
    }

    override fun deleteCartItem(
        cartId: Long,
        callback: (Long) -> Unit,
    ) {
        val removed = cartItems.removeIf { it.product.id == cartId }
        if (removed) callback(cartId)
    }

    override fun addCartItem(
        cartItem: CartItem,
        callback: () -> Unit,
    ) {
        cartItems.add(cartItem)
        callback()
    }

    override fun increaseCartItem(
        productId: Long,
        callback: (CartItem?) -> Unit,
    ) {
        val item = cartItems.find { it.product.id == productId }
        item?.let {
            val updated = it.copy(quantity = it.quantity + 1)
            cartItems.remove(it)
            cartItems.add(updated)
            callback(updated)
        } ?: callback(null)
    }

    override fun decreaseCartItem(
        productId: Long,
        callback: (CartItem?) -> Unit,
    ) {
        val item = cartItems.find { it.product.id == productId }
        item?.let {
            val updated = it.copy(quantity = it.quantity - 1)
            cartItems.remove(it)
            if (updated.quantity > 0) {
                cartItems.add(updated)
                callback(updated)
            } else {
                callback(null)
            }
        } ?: callback(null)
    }

    fun addTestCartItems(items: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(items)
    }
}
