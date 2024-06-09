package woowacourse.shopping.presentation.ui

import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.domain.OrderRepository

class FakeOrderRepository :
    OrderRepository {
    override suspend fun postOrders(orderRequest: OrderRequest): Result<Unit> {
        TODO("Not yet implemented")
    }
}
