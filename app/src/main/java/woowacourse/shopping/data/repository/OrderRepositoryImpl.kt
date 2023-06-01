package woowacourse.shopping.data.repository

import com.example.domain.model.Order
import com.example.domain.model.OrderDetail
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.data.dataSource.remote.order.OrderService

class OrderRepositoryImpl(
    private val orderService: OrderService
) : OrderRepository {
    override fun getAllOrders(onSuccess: (List<Order>) -> Unit, onFailure: () -> Unit) {
    }

    override fun getOrderDetailById(
        orderId: Long,
        onSuccess: (OrderDetail) -> Unit,
        onFailure: () -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun addOrder(
        cartIds: List<Long>,
        orderPaymentPrice: Int,
        onSuccess: (orderId: Long) -> Unit,
        onFailure: () -> Unit
    ) {
        TODO("Not yet implemented")
    }
}
