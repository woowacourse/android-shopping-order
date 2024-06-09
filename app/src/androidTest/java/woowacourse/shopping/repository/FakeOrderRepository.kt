package woowacourse.shopping.repository

import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.domain.repository.OrderRepository

class FakeOrderRepository :
    OrderRepository {
    override suspend fun post(orderRequest: OrderRequest): Result<Unit> {
        TODO("Not yet implemented")
    }
}
