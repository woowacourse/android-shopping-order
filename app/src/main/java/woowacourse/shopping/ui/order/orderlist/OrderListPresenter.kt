package woowacourse.shopping.ui.order.orderlist

import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.repository.UserRepository
import woowacourse.shopping.ui.order.uistate.OrderUIState.Companion.toUIState

class OrderListPresenter(
    private val view: OrderListContract.View,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : OrderListContract.Presenter {
    override fun loadOrders() {
        val currentUser = userRepository.findCurrent().getOrElse {
            return
        }
        orderRepository.findAll(currentUser).onSuccess { orders ->
            view.showOrders(orders.map { it.toUIState() })
        }.onFailure {
            return
        }
    }

    override fun openOrderDetail(orderId: Long) {
        view.showOrderDetail(orderId)
    }
}
