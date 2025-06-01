package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.CachedCartItem
import woowacourse.shopping.domain.model.PagingData

interface CartItemRepository {
    fun getInitialCartItems(
        page: Int?,
        size: Int?,
        onResult: (Result<List<CachedCartItem>>) -> Unit,
    )

    fun getCartItems(
        page: Int?,
        size: Int? = 5,
        onResult: (Result<PagingData>) -> Unit,
    )

    fun deleteCartItem(
        id: Long,
        onResult: (Result<Unit>) -> Unit,
    )

    fun addCartItem(
        id: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun updateCartItemQuantity(
        id: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun addCartItemQuantity(
        id: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun getCartItemsCount(onResult: (Result<Int>) -> Unit)

    fun getQuantity(pagingData: PagingData): PagingData

    fun getCartItemIds(): List<Long>
}
