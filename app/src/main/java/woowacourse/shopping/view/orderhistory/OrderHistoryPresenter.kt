package woowacourse.shopping.view.orderhistory

import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.model.OrderDetailModel

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    private val orderRepository: OrderRepository
) : OrderHistoryContract.Presenter {
    override fun fetchOrders() {
        orderRepository.getAll { ordersDto ->
            val orders = ordersDto.orders.map { OrderDetailModel.from(it) }
            view.showOrders(orders)
        }
    }
}
