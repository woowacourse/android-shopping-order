package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.PagingData

interface CartItemsRepository {
    suspend fun preloadCartItems(): Result<Unit>

    suspend fun getCartItems(
        page: Int?,
        size: Int? = 5,
    ): Result<PagingData>

    suspend fun deleteCartItem(id: Long): Result<Unit>

    suspend fun addCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun updateCartItemQuantity(
        id: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun addCartItemQuantity(
        id: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun getCartItemsCount(): Result<Int>

    suspend fun getQuantity(pagingData: PagingData): Result<PagingData>
}
