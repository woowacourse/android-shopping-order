package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.PagingData

interface CartItemRepository {
    fun getCartItems(
        page: Int,
        size: Int = 5,
        onResult: (Result<PagingData>) -> Unit,
    )

    fun deleteCartItem(
        id: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun addCartItem(
        id: Int,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun updateCartItem(
        id: Int,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun getCartItemsCount(onResult: (Result<Int>) -> Unit)
}
