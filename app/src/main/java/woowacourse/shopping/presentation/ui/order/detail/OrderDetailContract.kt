package woowacourse.shopping.presentation.ui.order.detail

import woowacourse.shopping.domain.model.Order

interface OrderDetailContract {
    interface View {
        fun showOrderDateTime(dateTime: String)
        fun showOrderDetail(orderDetail: List<Order.OrderedProduct>)
        fun showUnexpectedError()
    }

    interface Presenter {
        fun fetchOrderDetail(orderId: Long)
    }
}
