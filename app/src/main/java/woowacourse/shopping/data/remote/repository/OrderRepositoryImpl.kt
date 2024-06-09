package woowacourse.shopping.data.remote.repository

import woowacourse.shopping.data.remote.datasource.order.OrderDataSource
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.domain.OrderRepository

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
) : OrderRepository {
    override suspend fun postOrders(orderRequest: OrderRequest): Result<Unit> =
        runCatching {
            val response = orderDataSource.post(orderRequest)
            if (response.isSuccessful) {
                return Result.success(Unit)
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }
}
