package woowacourse.shopping.domain.model

import java.io.Serializable

class Order(cartItems: List<CartItem> = emptyList()) : Serializable {
    private val _list = cartItems.toMutableList()
    val list: List<CartItem>
        get() = _list.toList()

    fun size(): Int = _list.size

    fun addCartItem(cartItem: CartItem) {
        cartItem.isChecked = true
        _list.add(cartItem)
    }

    fun removeCartItem(cartItem: CartItem) {
        cartItem.isChecked = false
        _list.remove(cartItem)
    }

    fun addCount(cartItem: CartItem) {
        val index = _list.find { it.id == cartItem.id }?.let { _list.indexOf(it) } ?: -1
        if (index == -1) {
            addCartItem(cartItem)
        } else {
            _list[index] = cartItem.copy(quantity = cartItem.quantity)
        }
    }

    fun subCount(cartItem: CartItem) {
        val index = _list.find { it.id == cartItem.id }?.let { _list.indexOf(it) } ?: -1
        if (index != -1) {
            if (cartItem.quantity == 0) {
                removeCartItem(cartItem)
            } else {
                _list[index] = cartItem.copy(quantity = cartItem.quantity - 1)
            }
        }
    }

    fun clearOrder() {
        _list.forEach { it.isChecked = false }
        _list.clear()
    }

    fun selectAllCartItems(cartItems: List<CartItem>) {
        clearOrder()
        cartItems.forEach { addCartItem(it) }
    }

    fun getTotalPrice(): Long {
        return _list.sumOf { it.price * it.quantity }
    }

    fun getTotalQuantity(): Int {
        return _list.sumOf { it.quantity }
    }
}
