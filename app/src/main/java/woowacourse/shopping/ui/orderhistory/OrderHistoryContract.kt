package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.ui.model.OrderUiModel

interface OrderHistoryContract {

    interface View {

        fun initView(orders: List<OrderUiModel>)

        fun showErrorMessage(errorMessage: String)
    }

    interface Presenter {

        fun getOrders()
    }
}
