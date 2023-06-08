package woowacourse.shopping.presentation.ui.order

import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.OrderRepository

class OrderPresenter(
    private val view: OrderContract.View,
    private val orderRepository: OrderRepository,
) : OrderContract.Presenter {
    private var orders: List<Order> = emptyList()

    override fun fetchOrders() {
        orderRepository.fetchOrders { result ->
            result
                .onSuccess { view.showOrders(it) }
                .onFailure { view.showError(it.message ?: "에러 메시지가 없습니다.") }
        }
    }

    override fun selectOrder(position: Int) {
        view.showOrderDetail(orders[position].orderId)
    }
}
