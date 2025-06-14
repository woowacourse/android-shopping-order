package woowacourse.shopping.data.order.repository

import woowacourse.shopping.data.order.datasource.OrderRemoteDataSource
import woowacourse.shopping.data.order.remote.dto.OrderRequestDto

class DefaultOrderRepository(
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : OrderRepository {
    override suspend fun placeOrder(shoppingCartIds: List<Long>): Result<Unit> =
        runCatching {
            orderRemoteDataSource.postOrders(
                orderRequestDto = OrderRequestDto(shoppingCartIds),
            )
        }

    companion object {
        private var instance: OrderRepository? = null

        fun initialize(orderRemoteDataSource: OrderRemoteDataSource) {
            if (instance == null) {
                instance =
                    DefaultOrderRepository(
                        orderRemoteDataSource = orderRemoteDataSource,
                    )
            }
        }

        fun get(): OrderRepository = instance ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
