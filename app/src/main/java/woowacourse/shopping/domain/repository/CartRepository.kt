package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.Cart

interface CartRepository {
    suspend fun loadAll(): Result<List<Cart>>

    suspend fun getTotalCartItemCount(): Result<Int>

    suspend fun load(
        startPage: Int,
        pageSize: Int,
    ): Result<List<Cart>>

    suspend fun loadById(productId: Long): Result<Cart>

    suspend fun deleteExistCartItem(productId: Long): Result<Unit>

    suspend fun applyDeltaToCartQuantity(
        productId: Long,
        quantityDelta: Int,
    ): Result<Int>

    suspend fun setNewCartQuantity(
        productId: Long,
        newQuantity: Int,
    ): Result<Unit>
}
