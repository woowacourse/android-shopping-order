package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.order.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.request.OrderRequest
import woowacourse.shopping.data.mapper.toOrderUiModel
import woowacourse.shopping.ui.model.OrderUiModel

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : OrderRepository {

    override fun addOrder(
        basketIds: List<Int>,
        usingPoint: Int,
        totalPrice: Int,
        onAdded: (orderId: Int) -> Unit,
    ) {
        val orderRequest = OrderRequest(
            basketIds = basketIds.map(Int::toLong),
            usingPoint = usingPoint.toLong(),
            totalPrice = totalPrice.toLong()
        )

        orderRemoteDataSource.addOrder(orderRequest) { orderId ->
            onAdded(orderId.toInt())
        }
    }

    override fun getOrder(
        orderId: Int,
        onReceived: (order: OrderUiModel) -> Unit,
    ) {
        orderRemoteDataSource.getOrder(orderId) {
            val orderRecord = it.toOrderUiModel()

            onReceived(orderRecord)
        }
    }

    override fun getOrders(onReceived: (orders: List<OrderUiModel>) -> Unit) {
        orderRemoteDataSource.getOrders {
            val orders = it.map { orderResponse ->
                orderResponse.toOrderUiModel()
            }

            onReceived(orders)
        }
    }
}
