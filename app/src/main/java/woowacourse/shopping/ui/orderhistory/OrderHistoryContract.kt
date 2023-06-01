package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.model.UiOrder

interface OrderHistoryContract {
    interface View {
        fun showExtraOrderList(orders: List<UiOrder>)
        fun showLoadOrderFailed()
        fun navigateToOrderDetail(order: UiOrder)
        fun navigateToHome()
    }

    abstract class Presenter(protected val view: View) {
        abstract fun loadMoreOrderList()
        abstract fun navigateToHome()
        abstract fun inquiryOrderDetail(order: UiOrder)
    }
}
