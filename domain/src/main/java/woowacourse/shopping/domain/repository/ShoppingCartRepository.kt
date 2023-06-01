package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.util.WoowaResult

interface ShoppingCartRepository {
    fun fetchAll(callback: (WoowaResult<List<CartProduct>>) -> Unit)
    fun delete(callback: (WoowaResult<Boolean>) -> Unit, id: Long)
    fun insert(
        callback: (WoowaResult<Long>) -> Unit,
        productId: Long,
        quantity: Int = 1,
    )

    fun update(
        callback: (WoowaResult<Boolean>) -> Unit,
        productId: Long,
        updatedQuantity: Int,
    )
}
