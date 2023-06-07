package woowacourse.shopping.feature.order.list

import woowacourse.shopping.model.OrderMinInfoItemUiModel

interface OrderListContract {
    interface View {
        fun showFailedLoadOrderList()
        fun setOrderListItems(orderItems: List<OrderMinInfoItemUiModel>)
        fun showOrderDetail(orderId: Long)
        fun showNetworkError()
    }

    interface Presenter {
        fun loadOrderItems()
        fun requestOrderDetail(orderId: Long)
    }
}
