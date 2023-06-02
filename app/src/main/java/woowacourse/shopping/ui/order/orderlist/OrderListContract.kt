package woowacourse.shopping.ui.order.orderlist

import woowacourse.shopping.ui.ErrorView
import woowacourse.shopping.ui.order.uistate.OrderUIState

interface OrderListContract {
    interface Presenter {
        fun loadOrders()
        fun openOrderDetail(orderId: Long)
    }

    interface View : ErrorView {
        fun showOrders(orders: List<OrderUIState>)
        fun showOrderDetail(orderId: Long)
        override fun showError(message: String)
    }
}
