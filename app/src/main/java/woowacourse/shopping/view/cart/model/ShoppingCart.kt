package woowacourse.shopping.view.cart.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.domain.model.CartItem
import java.io.Serializable

class ShoppingCart : Serializable {
    private val _cartItems: MutableLiveData<List<CartItem>> = MutableLiveData(listOf())
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    fun updateProducts(cartItems: List<CartItem>) {
        _cartItems.value = emptyList()
        _cartItems.postValue(cartItems)
    }

    fun addProducts(cartItems: List<CartItem>) {
        _cartItems.value = cartItems
    }

    fun addProduct(cartItem: CartItem) {
        _cartItems.value = _cartItems.value?.plus(cartItem)
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
