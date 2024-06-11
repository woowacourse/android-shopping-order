package woowacourse.shopping.data.source

import woowacourse.shopping.domain.model.cart.CartItem

interface CartItemDataSource {
    suspend fun loadCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartItem>>

    suspend fun loadCartItem(productId: Long): Result<CartItem>

    suspend fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit>

    suspend fun deleteCartItem(id: Int): Result<Unit>

    suspend fun updateCartItem(
        id: Int,
        quantity: Int,
    ): Result<Unit>

    suspend fun loadCartItemCount(): Result<Int>
}
