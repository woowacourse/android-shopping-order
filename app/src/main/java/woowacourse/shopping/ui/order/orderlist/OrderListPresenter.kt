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
        userRepository.findCurrent().thenCompose {
            val currentUser = it.getOrThrow()
            orderRepository.findAll(currentUser)
        }.thenAccept { ordersResult ->
            val orders = ordersResult.getOrThrow()
            view.showOrders(orders.map { it.toUIState() })
        }.exceptionally {
            view.showError(it.message.orEmpty())
            null
        }
    }

    override fun openOrderDetail(orderId: Long) {
        view.showOrderDetail(orderId)
    }
}
