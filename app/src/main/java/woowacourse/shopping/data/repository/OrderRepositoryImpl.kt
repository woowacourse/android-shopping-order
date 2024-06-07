package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dto.OrderRequest
import woowacourse.shopping.data.remote.datasource.OrderDataSourceImpl
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderDataSourceImpl: OrderDataSourceImpl,
) : OrderRepository {
    override suspend fun postOrder(cartItemIds: List<Int>): Result<Unit> {
        return orderDataSourceImpl.postOrder(OrderRequest(cartItemIds))
    }
}
