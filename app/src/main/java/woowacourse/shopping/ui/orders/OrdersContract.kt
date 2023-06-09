package woowacourse.shopping.ui.orders

import woowacourse.shopping.ui.orders.uistate.OrdersItemUIState

interface OrdersContract {
    interface Presenter {
        fun onLoadOrders()
    }
    interface View {
        fun showOrders(orders: List<OrdersItemUIState>)
    }
}
