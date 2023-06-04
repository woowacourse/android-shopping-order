package woowacourse.shopping.data.repository.remote

import com.example.domain.model.Order
import com.example.domain.model.OrderDetail
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.data.service.order.OrderRemoteService

class OrderRepositoryImpl(private val service: OrderRemoteService) : OrderRepository {
    override fun addOrder(
        cartIds: List<Long>,
        totalPrice: Int,
        onSuccess: (orderId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        service.requestAddOrder(cartIds, totalPrice, onSuccess, onFailure)
    }

    override fun getAll(onSuccess: (orders: List<Order>) -> Unit, onFailure: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getOrderDetail(
        orderId: Long,
        onSuccess: (orderDetail: OrderDetail) -> Unit,
        onFailure: () -> Unit,
    ) {
        service.requestOrderDetail(orderId, onSuccess, onFailure)
    }
}
