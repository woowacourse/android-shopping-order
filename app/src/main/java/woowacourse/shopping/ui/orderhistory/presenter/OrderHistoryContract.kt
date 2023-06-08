package woowacourse.shopping.ui.orderhistory.presenter

import woowacourse.shopping.ui.orderhistory.uimodel.OrderHistory

interface OrderHistoryContract {
    interface View {
        fun setOrderHistory(history: List<OrderHistory>)
    }

    interface Presenter {
        fun fetchOrderHistory()
    }
}
