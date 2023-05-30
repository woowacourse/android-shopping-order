package woowacourse.shopping.ui.order.orderlist

import woowacourse.shopping.domain.user.User
import woowacourse.shopping.ui.order.uistate.OrderUIState

interface OrderListContract {
    interface Presenter {
        fun loadOrders()
        fun loadUsers()
        fun openOrderDetail(orderId: Long)
    }

    interface View {
        fun showUserList(users: List<User>)
        fun showOrders(orders: List<OrderUIState>)
        fun showOrderDetail(orderId: Long)
    }
}
