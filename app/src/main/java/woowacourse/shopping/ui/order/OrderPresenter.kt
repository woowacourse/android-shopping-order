package woowacourse.shopping.ui.order

import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.ui.order.uistate.OrderUIState

class OrderPresenter(
    private val view: OrderContract.View,
    private val orderRepository: OrderRepository
) : OrderContract.Presenter {
    override fun onLoadOrder(orderId: Long) {
        orderRepository.findById(orderId) { order ->
            view.showOrder(OrderUIState.from(order))
        }
    }

    override fun onLoadProduct(productId: Long) {
    }
}
