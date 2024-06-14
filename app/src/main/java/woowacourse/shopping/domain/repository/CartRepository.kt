package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result

interface CartRepository {
    suspend fun getCartItem(productId: Long): Result<CartWithProduct, DataError>

    suspend fun getAllCartItems(): Result<List<CartWithProduct>, DataError>

    suspend fun postCartItems(
        productId: Long,
        quantity: Int,
    ): Result<Unit, DataError>

    suspend fun deleteCartItem(id: Long): Result<Unit, DataError>

    suspend fun getCartItemsCount(): Result<Int, DataError>

    suspend fun patchCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit, DataError>

    suspend fun addProductToCart(
        productId: Long,
        quantity: Int,
    ): Result<Unit, DataError>
}
