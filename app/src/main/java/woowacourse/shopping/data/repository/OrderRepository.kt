package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.api.OrderApi
import woowacourse.shopping.data.model.request.OrderProductsRequest
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepository(
    private val api: OrderApi,
) : OrderRepository {
    override suspend fun postOrderProducts(cartIds: List<Long>): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                api.postOrderProducts(OrderProductsRequest(cartIds))
            }
        }
}
