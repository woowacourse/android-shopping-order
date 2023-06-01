package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.model.OrderModel

interface OrderHistoryContract {
    interface View {
        fun showExtraOrderList(orders: List<OrderModel>)
        fun showLoadOrderFailed()
        fun navigateToOrderDetail(order: OrderModel)
        fun navigateToHome()
    }

    abstract class Presenter(protected val view: View) {
        abstract fun loadMoreOrderList()
        abstract fun navigateToHome()
        abstract fun inquiryOrderDetail(order: OrderModel)
    }
}
