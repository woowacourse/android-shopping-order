package woowacourse.shopping.ui.order.orderdetail

import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.ui.order.uistate.OrderUIState.Companion.toUIState
import woowacourse.shopping.ui.order.uistate.PaymentUIState.Companion.toUIState

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository
) : OrderDetailContract.Presenter {
    override fun loadOrder(orderId: Long) {
        orderRepository.findById(orderId) { order ->
            orderRepository.findDiscountPolicy(order.price, "gold") { payment ->
                view.showOrder(order.toUIState(), payment.toUIState())
            }
        }
    }
}
