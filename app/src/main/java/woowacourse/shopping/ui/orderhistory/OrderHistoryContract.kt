package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.model.OrderModel

interface OrderHistoryContract {
    interface View {
        fun showMoreOrders(orders: List<OrderModel>)
        fun showLoadOrderFailed()
        fun navigateToOrderDetail(order: OrderModel)
        fun navigateToHome()
    }

    abstract class Presenter(protected val view: View) {
        abstract fun loadMoreOrders()
        abstract fun navigateToHome()
        abstract fun inquiryOrderDetail(order: OrderModel)
    }
}
