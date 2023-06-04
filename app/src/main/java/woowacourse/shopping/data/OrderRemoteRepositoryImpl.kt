package woowacourse.shopping.data

import com.example.domain.model.OrderDetailProduct
import com.example.domain.model.OrderHistoryInfo
import com.example.domain.model.OrderInfo
import com.example.domain.model.Point
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.data.service.OrderRemoteService

class OrderRemoteRepositoryImpl(
    private val service: OrderRemoteService
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

    override fun getOrderDetail(orderId: Int, callback: (Result<OrderInfo>) -> Unit) {
        service.loadDetail(orderId, callback)
    }
}
