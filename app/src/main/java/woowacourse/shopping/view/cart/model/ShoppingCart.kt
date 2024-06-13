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
        _cartItems.value = _cartItems.value?.plus(cartItem)
    }

    fun deleteProduct(itemId: Long) {
        _cartItems.value = _cartItems.value?.filter { it.id != itemId }
    }

    fun deleteProductFromProductId(productId: Long) {
        _cartItems.value = _cartItems.value?.filter { it.product.id != productId }
    }

    fun getTotalPrice(): Int {
        return cartItems.value
            ?.sumOf { it.product.cartItemCounter.itemCount * it.product.price } ?: 0
    }
}
