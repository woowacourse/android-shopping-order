package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct

interface CartProductRepository {
    suspend fun insert(
        productId: Int,
        quantity: Int,
    ): Result<Int>

    suspend fun getPagedProducts(
        page: Int? = null,
        size: Int? = null,
    ): Result<PagedResult<CartProduct>>

    suspend fun getCartProductByProductId(productId: Int): Result<CartProduct?>

    suspend fun getTotalQuantity(): Result<Int>

    suspend fun updateQuantity(
        cartProduct: CartProduct,
        quantityToAdd: Int,
    ): Result<Unit>

    suspend fun delete(id: Int): Result<Unit>
}
