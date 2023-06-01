package woowacourse.shopping.data.repository

import android.util.Log
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.request.OrderRequest
import woowacourse.shopping.data.mapper.toOrderDomainModel
import woowacourse.shopping.domain.Order

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
        Log.d("woogi", "addOrder: $orderRequest")

        orderRemoteDataSource.addOrder(orderRequest) { orderId ->
            onAdded(orderId.toInt())
        }
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
