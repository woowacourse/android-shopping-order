package woowacourse.shopping.data.cart.datasource

import woowacourse.shopping.data.cart.model.CartPageData

interface CartDataSource {
    suspend fun loadCarts(
        currentPage: Int,
        productSize: Int,
    ): Result<CartPageData>

    suspend fun loadTotalCarts(): Result<CartPageData>

    suspend fun createCartProduct(
        productId: Long,
        count: Int,
    ): Result<Long>

    suspend fun updateCartCount(
        cartId: Long,
        count: Int,
    ): Result<Unit>

    suspend fun deleteCartProduct(cartId: Long): Result<Unit>
}
