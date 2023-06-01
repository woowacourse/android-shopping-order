package woowacourse.shopping.ui.order

import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.ui.order.uistate.OrderResultUIState

class OrderResultPresenter(
    private val view: OrderResultContract.View,
    private val orderRepository: OrderRepository
) : OrderResultContract.Presenter {
    override fun onLoadOrder(orderId: Long) {
        orderRepository.findById(orderId) { order ->
            view.showOrder(OrderResultUIState.from(order))
        }
    }

    override fun onLoadProduct(productId: Long) {
    }
}
