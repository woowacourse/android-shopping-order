package woowacourse.shopping.feature.order.list

import woowacourse.shopping.model.OrderMinInfoItemUiModel

interface OrderListContract {
    interface View {
        fun setOrderListItems(orderItems: List<OrderMinInfoItemUiModel>)
        fun showOrderDetail(orderId: Long)
    }

    interface Presenter {
        fun loadOrderItems()
        fun requestOrderDetail(orderId: Long)
    }
}
