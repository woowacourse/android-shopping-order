package woowacourse.shopping.domain.repository

import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.domain.cart.CartItems

interface CartRepository {
    val cartItems: StateFlow<CartItems>

    suspend fun refreshCartItems()

    suspend fun addProduct(
        productId: Int,
        quantity: Int,
    )

    suspend fun updateQuantity(
        cartId: Int,
        quantity: Int,
    )

    suspend fun removeCartItem(cartId: Int)

    suspend fun order(cartItemIds: List<Int>)

    suspend fun getCartItemsByIds(cartIds: List<Int>): CartItems
}
