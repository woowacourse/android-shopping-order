package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.model.CartItem

interface CartRepository {
    val cartItems: StateFlow<List<CartItem>>

    suspend fun refreshCartItems()

    suspend fun setCartItem(
        productId: String,
        quantity: Int,
    )

    suspend fun deleteItem(cartItemId: String)

    suspend fun getCartItemQuantity(productId: String): Int?
}
