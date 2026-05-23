package woowacourse.shopping.domain.repository

import kotlinx.coroutines.flow.SharedFlow
import woowacourse.shopping.domain.model.cart.CartItems
import woowacourse.shopping.domain.model.cart.Quantity
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.ui.cart.PagedCartItems

interface CartRepository {
    val cartEvents: SharedFlow<Unit>

    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): PagedCartItems

    suspend fun getCartItemsCount(): Int

    suspend fun getAllCartItems(): CartItems

    suspend fun addProduct(
        product: Product,
        quantity: Quantity = Quantity.ONE,
    ): Int

    suspend fun increase(
        cartId: Int,
        quantity: Quantity,
    )

    suspend fun decrease(
        cartId: Int,
        quantity: Quantity,
    )

    suspend fun remove(cartId: Int)

    suspend fun order(cartItemIds: List<Int>)
}
