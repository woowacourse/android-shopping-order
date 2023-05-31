package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.util.WoowaResult

interface ShoppingCartRepository {
    fun fetchAll(callback: (WoowaResult<List<CartProduct>>) -> Unit)
    fun delete(callback: (WoowaResult<Boolean>) -> Unit, id: Int)
    fun insert(
        callback: (WoowaResult<Long>) -> Unit,
        productId: Int,
        quantity: Int = 1,
    )

    fun update(
        callback: (WoowaResult<Boolean>) -> Unit,
        productId: Int,
        updatedQuantity: Int,
    )
}
