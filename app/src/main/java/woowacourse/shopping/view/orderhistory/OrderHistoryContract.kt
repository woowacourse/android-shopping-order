package woowacourse.shopping.view.orderhistory

import woowacourse.shopping.model.OrderDetailModel

interface OrderHistoryContract {
    interface View {
        fun showOrders(orders: List<OrderDetailModel>)
        fun setUpPresenter()
        fun showNotSuccessfulErrorToast()
        fun showServerFailureToast()
        fun showServerResponseWrongToast()
    }
    interface Presenter {
        fun fetchOrders()
    }
}
