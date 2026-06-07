package woowacourse.shopping.viewmodel.fakes

import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.repository.OrderRepository
import woowacourse.shopping.domain.Order

class FakeOrderRepository : OrderRepository {
    override suspend fun order(order: Order): ApiResult<Unit> = ApiResult.Success(Unit)
}
