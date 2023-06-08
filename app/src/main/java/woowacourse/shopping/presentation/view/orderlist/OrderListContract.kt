package woowacourse.shopping.presentation.view.orderlist

import woowacourse.shopping.presentation.model.OrderDetailModel

interface OrderListContract {
    interface View {
        fun showOrderListItemView(orders: List<OrderDetailModel>)
        fun handleErrorView(messageId: Int)
    }

    interface Presenter {
        fun loadOrderList()
    }
}
