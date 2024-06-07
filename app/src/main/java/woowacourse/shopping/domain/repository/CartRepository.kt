package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Cart

interface CartRepository {
    suspend fun load(
        startPage: Int,
        pageSize: Int,
    ): Result<List<Cart>>

    suspend fun saveNewCartItem(
        productId: Long,
        incrementAmount: Int,
    ): Result<Long>

    suspend fun updateCartItemQuantity(
        cartId: Long,
        newQuantity: Int,
    ): Result<Unit>

    suspend fun deleteCartItem(cartId: Long): Result<Unit>

    suspend fun getCount(): Result<Int>
}
