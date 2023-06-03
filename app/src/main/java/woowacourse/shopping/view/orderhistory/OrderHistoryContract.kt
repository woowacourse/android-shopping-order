package woowacourse.shopping.view.orderhistory

import woowacourse.shopping.model.OrderDetailModel

interface OrderHistoryContract {
    interface View {
        fun showOrders(orders: List<OrderDetailModel>)
        fun setUpPresenter()
    }
    interface Presenter {
        fun fetchOrders()
    }
}
