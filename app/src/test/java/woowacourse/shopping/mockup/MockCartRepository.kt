package woowacourse.shopping.mockup

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.model.CartItem

class MockCartRepository(
    initialCartItems: List<CartItem> = emptyList(),
) : CartRepository {
    private val _cartItems = MutableStateFlow(initialCartItems)
    override val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _selectedCartItemIds = MutableStateFlow<ImmutableList<String>>(persistentListOf())
    override val selectedCartItemIds: StateFlow<ImmutableList<String>> = _selectedCartItemIds.asStateFlow()

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
        unselectCartItem(cartItemId)
    }

    override suspend fun getCartItemQuantity(productId: String): Int? =
        _cartItems.value.firstOrNull { it.product.id == productId }?.quantity

    override fun toggleCartItemSelection(cartItemId: String) {
        _selectedCartItemIds.update { selectedItemsId ->
            if (cartItemId in selectedItemsId) {
                (selectedItemsId - cartItemId).toImmutableList()
            } else {
                (selectedItemsId + cartItemId).toImmutableList()
            }
        }
    }

    override fun selectCartItem(cartItemId: String) {
        _selectedCartItemIds.update { selectedItemsId ->
            if (cartItemId in selectedItemsId) {
                selectedItemsId
            } else {
                (selectedItemsId + cartItemId).toImmutableList()
            }
        }
    }

    override fun unselectCartItem(cartItemId: String) {
        _selectedCartItemIds.update { selectedItemsId ->
            (selectedItemsId - cartItemId).toImmutableList()
        }
    }

    override fun selectAllCartItems() {
        _selectedCartItemIds.value = cartItems.value.map { it.id }.toImmutableList()
    }

    override fun clearCartItemSelection() {
        _selectedCartItemIds.value = persistentListOf()
    }
}
