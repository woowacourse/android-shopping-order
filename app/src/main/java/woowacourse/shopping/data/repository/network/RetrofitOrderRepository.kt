package woowacourse.shopping.data.repository.network

import woowacourse.shopping.data.remote.auth.BasicAuthEncoder
import woowacourse.shopping.data.remote.dto.OrderRequest
import woowacourse.shopping.data.remote.service.OrderService
import woowacourse.shopping.data.repository.OrderRepository

class RetrofitOrderRepository(
    private val encoder: BasicAuthEncoder,
    private val service: OrderService,
) : OrderRepository {
    override suspend fun requestOrder(ids: List<Long>) {
        service.requestOrder(
            auth = encoder.getHeader(),
            request = OrderRequest(ids),
        )
    }
}
