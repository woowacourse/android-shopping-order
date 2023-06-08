package woowacourse.shopping.data.repository.order

import com.example.domain.model.order.Order
import com.example.domain.model.order.OrderDetailProduct
import com.example.domain.model.order.OrderHistoryInfo
import com.example.domain.model.point.Point
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.data.datasource.remote.order.OrderDataSourceImpl

class OrderRemoteRepositoryImpl(
    private val service: OrderDataSourceImpl
) : OrderRepository {

    override fun addOrder(
        usedPoint: Point,
        orderDetailProduct: List<OrderDetailProduct>,
        callback: (Result<Unit>) -> Unit
    ) {
        service.addOrder(
            usedPoint,
            orderDetailProduct,
            callback
        )
    }

    override fun cancelOrder(
        orderId: Int,
        callback: (Result<Unit>) -> Unit
    ) {
        service.cancelOrder(orderId, callback)
    }

    override fun getOrderHistory(
        currentPageNum: Int,
        callback: (Result<OrderHistoryInfo>) -> Unit
    ) {
        service.loadAll(currentPageNum, callback)
    }

    override fun getOrderDetail(orderId: Int, callback: (Result<Order>) -> Unit) {
        service.loadDetail(orderId, callback)
    }
}
