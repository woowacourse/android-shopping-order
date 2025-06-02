package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.network.request.toRequest
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartsSinglePage
import woowacourse.shopping.domain.repository.CartRepository

class DefaultCartRepository(
    private val dataSource: CartDataSource,
) : CartRepository {
    override fun addCart(
        cart: Cart,
        callback: (Result<String>) -> Unit,
    ) {
        dataSource.addCart(cart.toRequest()) { callback(it) }
    }

    override fun loadSinglePage(
        page: Int?,
        pageSize: Int?,
        callback: (Result<CartsSinglePage>) -> Unit,
    ) {
        dataSource.singlePage(page, pageSize) { callback(it) }
    }

    override fun updateQuantity(
        cartId: Long,
        quantity: Quantity,
        callback: (Result<Unit>) -> Unit,
    ) {
        dataSource.updateCartQuantity(cartId, quantity.value) { callback(it) }
    }

    override fun deleteCart(
        cartId: Long,
        callback: (Result<Unit>) -> Unit,
    ) {
        dataSource.deleteCart(cartId) { callback(it) }
    }
}
