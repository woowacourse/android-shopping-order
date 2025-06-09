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
            val cartIds = ids.mapNotNull {
                cartItemsLocalDataSource.remove(it)
                cartItemsLocalDataSource.getCartId(it)
            }
            orderDataSource.orderProducts(cartIds)
        }
    }
}