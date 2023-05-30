package woowacourse.shopping.ui.order.orderlist

import woowacourse.shopping.domain.user.Rank
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.ui.order.uistate.OrderUIState.Companion.toUIState

class OrderListPresenter(
    private val view: OrderListContract.View,
    private val orderRepository: OrderRepository
) : OrderListContract.Presenter {
    override fun loadOrders() {
        orderRepository.findAll { orders ->
            view.showOrders(orders.map { it.toUIState() })
        }
    }

    override fun loadUsers() {
        val users =
            listOf(User(0, "First", Rank.FIFTH, "", ""), User(1, "Second", Rank.FIFTH, "", ""))
        view.showUserList(users)
    }

    override fun openOrderDetail(orderId: Long) {
        view.showOrderDetail(orderId)
    }
}
