package woowacourse.shopping.data.cart.datasource

import woowacourse.shopping.data.cart.model.CartPageData

interface CartDataSource {
    suspend fun loadCarts(
        currentPage: Int,
        productSize: Int,
    ): Result<CartPageData>

    fun loadTotalCarts(): Result<CartPageData>

    fun createCartProduct(
        productId: Long,
        count: Int,
    ): Result<Long>

    fun updateCartCount(
        cartId: Long,
        count: Int,
    ): Result<Unit>

    fun deleteCartProduct(cartId: Long): Result<Unit>
}
