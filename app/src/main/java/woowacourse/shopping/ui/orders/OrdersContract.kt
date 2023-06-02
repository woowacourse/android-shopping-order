package woowacourse.shopping.ui.orders

import woowacourse.shopping.model.OrderUIModel

interface OrdersContract {
    interface View {
        fun showOrderHistories(orders: List<OrderUIModel>)
    }

    interface Presenter {
        fun getOrderHistoryList()
    }
}
