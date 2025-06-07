package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Products

interface CartRepository {
    suspend fun fetchCartProducts(
        page: Int,
        size: Int,
    ): Result<Products>

    suspend fun fetchAllCartProducts(): Result<Products>

    suspend fun fetchCartItemCount(): Result<Int>

    suspend fun addCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Unit>

    suspend fun deleteCartProduct(cartId: Long): Result<Unit>

    suspend fun updateCartProduct(
        cartId: Long,
        quantity: Int,
    ): Result<Unit>
}
