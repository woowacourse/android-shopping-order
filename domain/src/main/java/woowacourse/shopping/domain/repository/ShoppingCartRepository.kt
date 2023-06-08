package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct

interface ShoppingCartRepository {
    fun fetchAll(callback: (Result<List<CartProduct>>) -> Unit)
    fun delete(callback: (Result<Boolean>) -> Unit, id: Long)
    fun insert(
        callback: (Result<Long>) -> Unit,
        productId: Long,
        quantity: Int = 1,
    )

    fun update(
        callback: (Result<Boolean>) -> Unit,
        id: Long,
        updatedQuantity: Int,
    )
}
