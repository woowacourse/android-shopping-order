package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.domain.model.CartItem

interface CartRemoteDataSource {
    suspend fun fetchTotalCount(): Result<Int>

    suspend fun fetchPagedCartItems(
        page: Int,
        size: Int?,
    ): Result<List<CartItem>>

    suspend fun insertCartItem(
        productId: Long,
        quantity: Int,
    ): Result<Long>

    suspend fun updateQuantity(
        cartId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun deleteCartItemById(cartId: Long): Result<Unit>
}
