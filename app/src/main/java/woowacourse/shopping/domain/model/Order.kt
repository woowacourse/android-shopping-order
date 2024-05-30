package woowacourse.shopping.domain.model

class Order {
    private val _list = mutableListOf<CartItem>()
    val list: List<CartItem>
        get() = _list

    fun addCartItem(cartItem: CartItem) {
        _list.add(cartItem)
    }

    fun removeCartItem(cartItem: CartItem) {
        _list.remove(cartItem)
    }

    fun clearOrder() {
        _list.clear()
    }

    fun selectAllCartItems(cartItems: List<CartItem>) {
        clearOrder()
        _list.addAll(cartItems)
    }

    fun getTotalPrice(): Long {
        return _list.sumOf { it.price * it.quantity }
    }

    fun getTotalQuantity(): Int {
        return _list.sumOf { it.quantity }
    }
}
