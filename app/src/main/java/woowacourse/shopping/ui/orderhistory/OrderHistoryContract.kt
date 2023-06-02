package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.model.OrderModel

interface OrderHistoryContract {
    interface View {
        fun showMoreOrders(orders: List<OrderModel>)
        fun showLoadOrderFailed()
        fun navigateToOrderDetail(order: OrderModel)
        fun navigateToHome()
    }

    interface Presenter {
        fun loadMoreOrders()
        fun navigateToHome()
        fun inquiryOrderDetail(order: OrderModel)
    }
}
