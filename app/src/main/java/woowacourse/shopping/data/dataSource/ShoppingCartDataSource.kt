package woowacourse.shopping.data.dataSource

import woowacourse.shopping.data.dto.ShoppingCartDto
import woowacourse.shopping.domain.util.WoowaResult

interface ShoppingCartDataSource {
    fun fetchAll(callback: (WoowaResult<List<ShoppingCartDto>>) -> Unit)
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
