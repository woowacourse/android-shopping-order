package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.order.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.request.OrderRequest
import woowacourse.shopping.data.mapper.toOrderDomainModel
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : OrderRepository {

    override fun addOrder(
        basketIds: List<Int>,
        usingPoint: Int,
        totalPrice: Int,
        onAdded: (orderId: Int) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    ) {
        val orderRequest = OrderRequest(
            basketIds = basketIds.map(Int::toLong),
            usingPoint = usingPoint.toLong(),
            totalPrice = totalPrice.toLong()
        )

        orderRemoteDataSource.addOrder(
            orderRequest = orderRequest,
            onAdded = { orderId ->
                onAdded(orderId.toInt())
            },
            onFailed = { errorMessage ->
                onFailed(errorMessage)
            }
        )
    }

    override fun getOrder(
        orderId: Int,
        onReceived: (order: Order) -> Unit,
    ) {
        orderRemoteDataSource.getOrder(orderId) {
            val orderRecord = it.toOrderDomainModel()

            onReceived(orderRecord)
        }
    }

    override fun getOrders(onReceived: (orders: List<Order>) -> Unit) {
        orderRemoteDataSource.getOrders {
            val orders = it.map { orderResponse ->
                orderResponse.toOrderDomainModel()
            }

            onReceived(orders)
        }
    }
}
