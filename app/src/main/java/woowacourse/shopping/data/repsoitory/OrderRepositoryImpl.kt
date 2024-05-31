package woowacourse.shopping.data.repsoitory

import woowacourse.shopping.data.datasource.remote.OrderDataSource
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
) : OrderRepository {
    override fun insertOrder(cartItemsIds: List<Int>): Result<Unit> =
        runCatching {
            orderDataSource.postOrder(cartItemsIds)
        }

    companion object {
        private var instance: OrderRepositoryImpl? = null

        fun setInstance(orderDataSource: OrderDataSource) {
            instance = OrderRepositoryImpl(orderDataSource = orderDataSource)
        }

        fun getInstance(): OrderRepositoryImpl = requireNotNull(instance)
    }
}
