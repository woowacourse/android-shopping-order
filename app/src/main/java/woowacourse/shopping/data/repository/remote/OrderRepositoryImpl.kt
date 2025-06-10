package woowacourse.shopping.data.repository.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.datasource.remote.OrderDataSource
import woowacourse.shopping.data.dto.order.CreateOrderRequest
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : OrderRepository {
    override suspend fun createOrder(cartItemIds: List<Long>) =
        withContext(defaultDispatcher) {
            val createOrderRequest = CreateOrderRequest(cartItemIds)
            orderDataSource.createOrder(createOrderRequest)
        }
}
