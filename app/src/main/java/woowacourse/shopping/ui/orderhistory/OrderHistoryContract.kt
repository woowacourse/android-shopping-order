package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.ui.model.UiOrder

interface OrderHistoryContract {
    interface View {
        fun updateOrdersInfo(ordersInfo: List<UiOrder>)
    }

    interface Presenter {
        val view: View
    }
}
