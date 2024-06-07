package woowacourse.shopping.view.cart.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.domain.model.CartItem
import java.io.Serializable

class ShoppingCart : Serializable {
    private val _cartItems: MutableLiveData<List<CartItem>> = MutableLiveData(listOf())
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    fun addProducts(cartItems: List<CartItem>) {
        _cartItems.value = cartItems
    }

    fun addProduct(cartItem: CartItem) {
        val existingCartItem = _cartItems.value?.find { it.id == cartItem.id }
        if (existingCartItem != null) {
            existingCartItem.product.cartItemCounter.updateCount(cartItem.product.cartItemCounter.itemCount)
        } else {
            _cartItems.value = _cartItems.value?.plus(cartItem)
        }
        _cartItems.postValue(_cartItems.value)
    }

    fun deleteProduct(itemId: Long) {
        _cartItems.value = _cartItems.value?.filter { it.id != itemId }
        _cartItems.postValue(_cartItems.value)
    }

    fun deleteProductFromProductId(productId: Long) {
        _cartItems.value = _cartItems.value?.filter { it.product.id != productId }
        _cartItems.postValue(_cartItems.value)
    }
}
