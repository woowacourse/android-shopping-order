package woowacourse.shopping.feature.orderHistory

import woowacourse.shopping.model.OrderHistoryProductUiModel

interface OrderHistoryContract {

    interface View {
        fun addOrderHistory(orderHistory: List<OrderHistoryProductUiModel>)
        fun showErrorMessage(t: Throwable)
    }

    interface Presenter {
        fun loadOrderHistory()
    }
}
