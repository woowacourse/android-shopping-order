package woowacourse.shopping.data.repository.cart

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.model.CartItem

interface CartRepository {
    val cartItems: StateFlow<List<CartItem>>
    val selectedCartItemIds: StateFlow<ImmutableList<String>>

    suspend fun refreshCartItems()

    suspend fun setCartItem(
        productId: String,
        quantity: Int,
    )

    suspend fun deleteItem(cartItemId: String)

    suspend fun deleteSelectedItems()

    suspend fun getCartItemQuantity(productId: String): Int?

    fun toggleCartItemSelection(cartItemId: String)

    fun selectCartItem(cartItemId: String)

    fun unselectCartItem(cartItemId: String)

    fun selectAllCartItems()

    fun clearCartItemSelection()
}
