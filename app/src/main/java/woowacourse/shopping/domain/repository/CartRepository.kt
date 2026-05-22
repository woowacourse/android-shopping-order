package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.cart.CartItems
import woowacourse.shopping.domain.model.cart.Quantity
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.ui.cart.PagedCartItems

interface CartRepository {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): PagedCartItems

    suspend fun getCartItemsCount(): Int

    suspend fun getAllCartItems(): CartItems

    suspend fun addProduct(
        product: Product,
        quantity: Quantity = Quantity.ONE,
    )

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
