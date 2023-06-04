package woowacourse.shopping.ui.orderlist

import woowacourse.shopping.uimodel.OrderHistoryUIModel

interface OrderListContract {
    interface View {
        fun setOrderList(orderHistories: List<OrderHistoryUIModel>)
    }

    interface Presenter {
        fun getOrderList()
    }
}
