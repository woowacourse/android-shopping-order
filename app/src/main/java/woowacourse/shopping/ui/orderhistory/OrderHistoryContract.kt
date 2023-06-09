package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.ui.model.OrderHistoryModel

interface OrderHistoryContract {
    interface Presenter {
        fun loadHistories()

        fun openDetail(id: Int)
    }

    interface View {
        fun showHistories(histories: List<OrderHistoryModel>)

        fun notifyFailure(message: String)

        fun showDetail(id: Int)
    }
}