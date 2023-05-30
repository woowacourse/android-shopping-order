package woowacourse.shopping.ui.order.orderdetail

import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.ui.order.uistate.OrderUIState.Companion.toUIState

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository
) : OrderDetailContract.Presenter {
    override fun loadOrder(orderId: Long) {
        orderRepository.findById(orderId) { order ->
            view.showOrder(order.toUIState())
        }
    }
}
