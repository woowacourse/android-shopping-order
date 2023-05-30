package woowacourse.shopping.ui.orderlist

import woowacourse.shopping.ui.orderlist.uistate.OrderUIState

interface OrderListContract {
    interface Presenter {
        fun loadOrders()
    }

    interface View {
        fun showOrders(orders: List<OrderUIState>)
    }
}
