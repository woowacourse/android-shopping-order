package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.entity.CartProduct

interface CartRepository {
    suspend fun cartProducts(
        currentPage: Int,
        pageSize: Int,
    ): Result<List<CartProduct>>

    fun totalCartProducts(): Result<List<CartProduct>>

    suspend fun filterCartProducts(productIds: List<Long>): Result<List<CartProduct>>

    suspend fun updateCartProduct(
        productId: Long,
        count: Int,
    ): Result<Unit>

    suspend fun deleteCartProduct(productId: Long): Result<Unit>

    fun canLoadMoreCartProducts(
        currentPage: Int,
        pageSize: Int,
    ): Result<Boolean>

    fun orderCartProducts(productIds: List<Long>): Result<Unit>
}
