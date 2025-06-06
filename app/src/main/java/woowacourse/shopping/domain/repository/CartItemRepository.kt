package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.CachedCartItem
import woowacourse.shopping.domain.model.PagingData

interface CartItemRepository {
    suspend fun getInitialCartItems(
        page: Int?,
        size: Int?,
    ): Result<List<CachedCartItem>>

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

    fun getQuantity(pagingData: PagingData): PagingData

    fun getCartItemProductIds(): List<Long>

    fun getCartItemCartIds(): List<Long>
}
