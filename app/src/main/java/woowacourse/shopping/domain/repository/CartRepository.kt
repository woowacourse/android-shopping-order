package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.cart.CartItems

interface CartRepository {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): CartItems

    suspend fun getCartItemsCount(): Int

    suspend fun getAllCartItems(): CartItems

    suspend fun addProduct(
        productId: Int,
        quantity: Int,
    )

    suspend fun updateQuantity(
        cartId: Int,
        quantity: Int,
    )

    suspend fun remove(cartId: Int)

    suspend fun order(cartItemIds: List<Int>)
}

sealed interface CartDecreaseResult {
    object Deleted : CartDecreaseResult

    object Decreased : CartDecreaseResult
}
