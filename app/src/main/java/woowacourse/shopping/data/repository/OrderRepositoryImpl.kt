package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.local.cart.CartItemsLocalDataSource
import woowacourse.shopping.data.source.remote.order.OrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
    private val cartItemsLocalDataSource: CartItemsLocalDataSource,
) : OrderRepository {
    override suspend fun orderProducts(ids: List<Long>): Result<Unit> {
        return runCatching {
            val cartIds = ids.map { productId ->
                val cartId = cartItemsLocalDataSource.getCartId(productId)
                requireNotNull(cartId)
            }

            orderDataSource.orderProducts(cartIds)
            ids.forEach { cartItemsLocalDataSource.remove(it) }
        }
    }
}