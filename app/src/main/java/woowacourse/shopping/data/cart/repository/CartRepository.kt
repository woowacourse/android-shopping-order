package woowacourse.shopping.data.cart.repository

import woowacourse.shopping.domain.Pageable
import woowacourse.shopping.domain.cart.CartItem

interface CartRepository {
    suspend fun loadPageableCartItems(
        page: Int,
        size: Int,
    ): Result<Pageable<CartItem>>

    suspend fun loadCart(): Result<List<CartItem>>

    suspend fun addCartItem(
        productId: Long,
        quantity: Int,
    ): Result<Long?>

    suspend fun remove(cartItemId: Long): Result<Unit>

    suspend fun updateCartItemQuantity(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit>
}
