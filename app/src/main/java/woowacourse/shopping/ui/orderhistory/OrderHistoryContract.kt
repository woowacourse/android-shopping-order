package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.ui.model.Order

interface OrderHistoryContract {

    interface View {

        fun setUpView(orders: List<Order>)
    }

    interface Presenter {

        fun getOrders()
    }
}
