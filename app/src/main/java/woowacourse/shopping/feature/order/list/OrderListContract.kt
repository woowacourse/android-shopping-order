package woowacourse.shopping.feature.order.list

import woowacourse.shopping.model.OrderPreviewUiModel

interface OrderListContract {
    interface View {
        fun showOrders(orderPreviews: List<OrderPreviewUiModel>)
        fun showOrderDetail(orderId: Long)
    }

    interface Presenter {
        fun requestOrders()
        fun requestOrderDetail(orderId: Long)
    }
}
