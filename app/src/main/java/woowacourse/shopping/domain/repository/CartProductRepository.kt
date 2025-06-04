package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct

interface CartProductRepository {
    fun insert(
        productId: Int,
        quantity: Int,
        onResult: (Result<Int>) -> Unit,
    )

    fun getPagedProducts(
        page: Int? = null,
        size: Int? = null,
        onResult: (Result<PagedResult<CartProduct>>) -> Unit,
    )

    fun getCartProductByProductId(
        productId: Int,
        onResult: (Result<CartProduct?>) -> Unit,
    )

    fun getTotalQuantity(onResult: (Result<Int>) -> Unit)

    fun updateQuantity(
        cartProduct: CartProduct,
        quantityToAdd: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun delete(
        id: Int,
        onResult: (Result<Unit>) -> Unit,
    )

    fun deleteAll(
        ids: Set<Int>,
        onResult: (Result<Unit>) -> Unit,
    )
}
