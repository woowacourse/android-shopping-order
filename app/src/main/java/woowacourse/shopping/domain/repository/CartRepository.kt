package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.AddItemResult
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.RemoveItemResult
import woowacourse.shopping.domain.model.UpdateItemResult

interface CartRepository {
    suspend fun getCart(): Cart

    suspend fun addItem(
        id: Long,
        quantity: Int = 1,
    ): AddItemResult

    suspend fun deleteItem(productId: Long): RemoveItemResult

    suspend fun changeCartItem(
        productId: Long,
        amount: Int,
    ): UpdateItemResult
}
