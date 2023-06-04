package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.ui.model.OrderHistoryModel

interface OrderHistoryContract {
    interface Presenter {
        fun loadHistories()
    }

    interface View {
        fun showHistories(histories: List<OrderHistoryModel>)

        fun notifyLoadFailed()
    }
}