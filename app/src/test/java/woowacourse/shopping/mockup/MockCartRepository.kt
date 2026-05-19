package woowacourse.shopping.mockup

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.model.CartItem

class MockCartRepository(
    initialCartItems: List<CartItem> = emptyList(),
) : CartRepository {
    private val _cartItems = MutableStateFlow(initialCartItems)
    override val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    override suspend fun refreshCartItems() = Unit

    override suspend fun setCartItem(
        productId: String,
        quantity: Int,
    ) {
        _cartItems.value =
            _cartItems.value.map { cartItem ->
                if (cartItem.product.id == productId) {
                    cartItem.copy(quantity = quantity)
                } else {
                    cartItem
                }
            }
    }

    override suspend fun deleteItem(cartItemId: String) {
        _cartItems.value = _cartItems.value.filterNot { it.id == cartItemId }
    }

    override suspend fun getCartItemQuantity(productId: String): Int? =
        _cartItems.value.firstOrNull { it.product.id == productId }?.quantity
}
