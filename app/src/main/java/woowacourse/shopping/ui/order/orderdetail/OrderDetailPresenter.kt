package woowacourse.shopping.ui.order.orderdetail

import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.repository.UserRepository
import woowacourse.shopping.ui.order.uistate.OrderUIState.Companion.toUIState
import woowacourse.shopping.ui.order.uistate.PaymentUIState.Companion.toUIState

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : OrderDetailContract.Presenter {
    override fun loadOrder(orderId: Long) {
        userRepository.findCurrent { user ->
            orderRepository.findById(orderId, user) { order ->
                orderRepository.findDiscountPolicy(order.price, user.rank.toString()) { payment ->
                    view.showOrder(order.toUIState(), payment.toUIState())
                }
            }
        }
    }
}
