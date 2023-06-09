package woowacourse.shopping.ui.orderlist

import woowacourse.shopping.uimodel.OrderHistoryUIModel

interface OrderListContract {
    interface View {
        fun setOrderList(orderHistories: List<OrderHistoryUIModel>)
        fun navigateToOrderDetail(orderId: Int)
    }

    interface Presenter {
        fun getOrderList()
        fun showOrderDetail(orderId: Int)
    }
}
