package woowacourse.shopping.presentation.view.orderlist

import woowacourse.shopping.presentation.model.OrderDetailModel

interface OrderListContract {
    interface View {
        fun showOrderListItemView(orders: List<OrderDetailModel>)
        fun handleErrorView(message: String)
    }

    interface Presenter {
        fun loadOrderList()
    }
}
