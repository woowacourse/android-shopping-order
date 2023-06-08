package woowacourse.shopping.presentation.ui.order.detail

import woowacourse.shopping.domain.model.Order

interface OrderDetailContract {
    interface View {
        fun showOrderDateTime(dateTime: String)
        fun showOrderDetail(orderDetail: List<Order.OrderedProduct>)
        fun showError(message: String)
    }

    interface Presenter {
        fun fetchOrderDetail(orderId: Long)
    }
}
