package woowacourse.shopping.data.repository

import woowacourse.shopping.data.api.OrderApi
import woowacourse.shopping.data.model.request.OrderProductsRequest
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepository(
    private val api: OrderApi,
) : OrderRepository {
    override suspend fun postOrderProducts(cartIds: List<Long>): Result<Unit> =
        runCatching {
            val response = api.postOrderProducts(OrderProductsRequest(cartIds))

            if (response.isSuccessful) {
                Unit
            } else {
                throw IllegalArgumentException()
            }
        }
}
