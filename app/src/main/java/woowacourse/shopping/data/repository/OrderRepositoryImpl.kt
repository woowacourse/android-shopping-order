package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.remote.order.OrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
) : OrderRepository {
    override fun orderItems(
        cartIds: List<Long>,
        onResult: (Result<Unit>) -> Unit,
    ) {
        orderDataSource.orderCheckedItems(cartIds) { result ->
            onResult(result)
        }
    }
}
