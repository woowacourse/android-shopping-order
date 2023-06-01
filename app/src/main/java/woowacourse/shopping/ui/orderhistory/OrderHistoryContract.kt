package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.ui.model.OrderUiModel

interface OrderHistoryContract {

    interface View {

        fun initView(orders: List<OrderUiModel>)
    }

    interface Presenter {

        fun getOrders()
    }
}
