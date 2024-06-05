package woowacourse.shopping.domain.model

class Order {
    private val _map = mutableMapOf<Long, CartItem>()
    val map: Map<Long, CartItem>
        get() = _map

    fun containsCartItem(cartItem: CartItem): Boolean {
        return map.contains(cartItem.id)
    }

    fun addCartItem(cartItem: CartItem) {
        _map[cartItem.id] = cartItem
    }

    fun removeCartItem(cartItemId: Long) {
        _map.remove(cartItemId)
    }

    fun clearOrder() {
        _map.clear()
    }

    fun selectAllCartItems(cartItems: List<CartItem>) {
        cartItems.forEach { addCartItem(it) }
    }

    fun getTotalPrice(): Long {
        return _map.values.sumOf { it.price * it.quantity }
    }

    fun getTotalQuantity(): Int {
        return _map.values.sumOf { it.quantity }
    }
}
