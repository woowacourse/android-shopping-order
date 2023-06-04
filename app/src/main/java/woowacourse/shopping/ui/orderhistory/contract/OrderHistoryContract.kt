package woowacourse.shopping.ui.orderhistory.contract

import woowacourse.shopping.model.OrderUIModel

interface OrderHistoryContract {
    interface View {
        fun setOrderHistory(orders: List<OrderUIModel>)
    }

    interface Presenter {
        fun getOrderHistory()
    }
}
