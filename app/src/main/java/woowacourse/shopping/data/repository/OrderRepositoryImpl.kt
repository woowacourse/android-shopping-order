package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.api.OrderApi
import woowacourse.shopping.data.remote.dto.request.CreateOrderRequestBody
import kotlin.coroutines.cancellation.CancellationException

class OrderRepositoryImpl(
    private val orderApi: OrderApi,
) : OrderRepository {
    override suspend fun createOrder(cartItemIds: List<Long>): Result<Unit> =
        try {
            orderApi.createOrder(
                CreateOrderRequestBody(cartItemIds = cartItemIds),
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
