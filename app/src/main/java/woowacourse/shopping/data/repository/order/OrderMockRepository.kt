package woowacourse.shopping.data.repository.order

import com.example.domain.model.Order
import com.example.domain.repository.OrderRepository

class OrderMockRepository : OrderRepository {
    override fun getOrders(page: Int, onSuccess: (List<Order>) -> Unit, onFailure: () -> Unit) {

    }
}
