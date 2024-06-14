package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.RemoteOrderDataSource
import woowacourse.shopping.data.model.cart.CartItemIds
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val remoteOrderDataSource: RemoteOrderDataSource,
) : OrderRepository {
    override suspend fun postOrder(cartItemIds: List<Int>): Result<Unit> {
        return runCatching {
            remoteOrderDataSource.postOrder(CartItemIds(cartItemIds))
        }
    }
}
