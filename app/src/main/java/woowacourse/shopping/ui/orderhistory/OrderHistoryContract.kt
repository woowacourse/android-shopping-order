package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.ui.model.Order

interface OrderHistoryContract {

    interface View {

        fun initView(orders: List<Order>)
    }

    interface Presenter {

        fun getOrders()
    }
}
