package woowacourse.shopping.data.repository.order

import com.example.domain.datasource.orderDataSource
import com.example.domain.model.Order
import com.example.domain.repository.OrderRepository

class OrderMockRepository : OrderRepository {
    override fun getOrders(page: Int, onSuccess: (List<Order>) -> Unit, onFailure: () -> Unit) {
        val fromIndex = (page - 1) * 10
        val toIndex = fromIndex + 10
        onSuccess(orderDataSource.subList(fromIndex, toIndex))
    }
}
