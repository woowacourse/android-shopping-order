package woowacourse.shopping.ui.orders

import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.ui.orders.uistate.OrdersItemUIState

class OrdersPresenter(
    private val view: OrdersContract.View,
    private val orderRepository: OrderRepository
) : OrdersContract.Presenter {
    override fun onLoadOrders() {
        orderRepository.findAll { orders ->
            val ordersItemUIStates = orders.sortedBy { -it.id }
                .map(OrdersItemUIState::from)
            view.showOrders(ordersItemUIStates)
        }
    }
}
