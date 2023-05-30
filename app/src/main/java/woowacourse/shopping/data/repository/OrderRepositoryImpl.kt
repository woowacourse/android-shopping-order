package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.order.OrderRemoteDataSource
import woowacourse.shopping.data.mapper.toOrderRecord
import woowacourse.shopping.data.model.OrderRequest
import woowacourse.shopping.ui.model.Order

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
        onReceived: (order: Order) -> Unit,
    ) {
        orderRemoteDataSource.getOrderRecord(orderId) { dataOrderRecord ->
            val orderRecord = dataOrderRecord.toOrderRecord()

            onReceived(orderRecord)
        }
    }
}
