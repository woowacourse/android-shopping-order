package woowacourse.shopping.ui.order.orderlist

import woowacourse.shopping.ui.order.uistate.OrderUIState

interface OrderListContract {
    interface Presenter {
        fun loadOrders()
    }

    interface View {
        fun showOrders(orders: List<OrderUIState>)
    }
}
