package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.OrderRemoteDataSource
import woowacourse.shopping.domain.repository.OrderRepository
import kotlin.concurrent.thread

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : OrderRepository {
    override fun addOrder(
        cartItemIds: List<String>,
        onResult: (Result<Unit>) -> Unit,
    ) {
        thread {
            onResult(orderRemoteDataSource.addOrder(cartItemIds))
        }
    }
}
