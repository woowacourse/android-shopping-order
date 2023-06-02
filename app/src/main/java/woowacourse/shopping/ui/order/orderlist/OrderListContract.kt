package woowacourse.shopping.ui.order.orderlist

import woowacourse.shopping.ui.order.uistate.OrderUIState

interface OrderListContract {
    interface Presenter {
        fun loadOrders()
        fun openOrderDetail(orderId: Long)
    }

    interface View {
        fun showOrders(orders: List<OrderUIState>)
        fun showOrderDetail(orderId: Long)
        fun showError(message: String)
    }
}
