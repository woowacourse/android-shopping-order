package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.model.OrderModel

interface OrderHistoryContract {
    interface View {
        fun showOrders(orders: List<OrderModel>)
        fun navigateToOrderDetail(order: OrderModel)
        fun navigateToHome()
        fun showLoading()
        fun hideLoading()
    }

    interface Presenter {
        fun loadMoreOrders()
        fun navigateToHome()
        fun inquiryOrderDetail(order: OrderModel)
    }
}
