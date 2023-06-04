package woowacourse.shopping.presentation.ui.order

import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.util.WoowaResult

class OrderPresenter(
    private val view: OrderContract.View,
    private val orderRepository: OrderRepository,
) : OrderContract.Presenter {
    private var orders: List<Order> = emptyList()

    override fun fetchOrders() {
        orderRepository.fetchOrders { result ->
            when (result) {
                is WoowaResult.SUCCESS -> {
                    orders = result.data
                    view.showOrders(orders)
                }
                is WoowaResult.FAIL -> view.showUnexpectedError()
            }
        }
    }

    override fun selectOrder(position: Int) {
        view.showOrderDetail(orders[position].orderId)
    }
}
