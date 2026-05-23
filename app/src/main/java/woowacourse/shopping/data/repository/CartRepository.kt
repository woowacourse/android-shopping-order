package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.RemoveItemResult

interface CartRepository {
    suspend fun addItem(
        id: Long,
        quantity: Int = 1,
    )

    suspend fun deleteItem(productId: Long): RemoveItemResult

    suspend fun changeCartItem(
        productId: Long,
        amount: Int,
    )

    suspend fun loadCart()

    val cart: StateFlow<Cart>
}
