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
        val currentUser = userRepository.findCurrent().getOrElse {
            return
        }
        val order = orderRepository.findById(orderId, currentUser).getOrElse {
            return
        }
        orderRepository.findDiscountPolicy(order.price, currentUser.rank.toString())
            .onSuccess { payment ->
                view.showOrder(order.toUIState(), payment.toUIState())
            }.onFailure {
                return
            }
    }
}
