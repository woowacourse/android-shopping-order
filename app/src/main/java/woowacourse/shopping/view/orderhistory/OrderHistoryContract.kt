package woowacourse.shopping.view.orderhistory

import woowacourse.shopping.model.OrderDetailModel

interface OrderHistoryContract {
    interface View {
        fun showOrders(orders: List<OrderDetailModel>)
        fun setUpPresenter()
        fun showErrorMessageToast(message: String?)
    }
    interface Presenter {
        fun fetchOrders()
    }
}
