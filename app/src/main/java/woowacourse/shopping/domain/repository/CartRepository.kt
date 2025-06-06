package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product

interface CartRepository {
    suspend fun fetchTotalCount(): Result<Int>

    suspend fun fetchPagedCartItems(
        page: Int,
        pageSize: Int? = null,
    ): Result<List<CartItem>>

    suspend fun fetchAllCartItems(): Result<Unit>

    fun fetchCartItemById(productId: Long): CartItem?

    suspend fun insertOrUpdate(
        product: Product,
        productQuantity: Int,
    ): Result<Unit>

    suspend fun insertProduct(
        product: Product,
        productQuantity: Int,
    ): Result<Unit>

    suspend fun updateProduct(
        cartId: Long,
        product: Product,
        quantity: Int,
    ): Result<Unit>

    suspend fun increaseQuantity(productId: Long): Result<Unit>

    suspend fun decreaseQuantity(productId: Long): Result<Unit>

    suspend fun deleteProduct(productId: Long): Result<Unit>
}
