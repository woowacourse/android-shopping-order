package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.ui.model.OrderHistoryModel

interface OrderHistoryContract {
    interface Presenter {
        fun loadHistories()

        fun openDetail(id: Int)
    }

    interface View {
        fun showHistories(histories: List<OrderHistoryModel>)

        fun notifyLoadFailed()

        fun showDetail(id: Int)
    }
}