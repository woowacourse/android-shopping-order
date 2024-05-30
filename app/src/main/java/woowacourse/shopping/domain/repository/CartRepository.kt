package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.entity.CartProduct

interface CartRepository {
    fun cartProducts(
        currentPage: Int,
        pageSize: Int,
    ): Result<List<CartProduct>>

    fun totalCartProducts(): Result<List<CartProduct>>

    fun filterCartProducts(productIds: List<Long>): Result<List<CartProduct>>

    fun updateCartProduct(
        productId: Long,
        count: Int,
    ): Result<Unit>

    fun deleteCartProduct(productId: Long): Result<Unit>

    fun canLoadMoreCartProducts(
        currentPage: Int,
        pageSize: Int,
    ): Result<Boolean>

    fun orderCartProducts(productIds: List<Long>): Result<Unit>
}
