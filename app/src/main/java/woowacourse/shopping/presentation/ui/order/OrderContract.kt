package woowacourse.shopping.presentation.ui.order

import woowacourse.shopping.domain.model.Order

interface OrderContract {
    interface View {
        fun showOrders(orders: List<Order>)
        fun showUnexpectedError()
        fun showOrderDetail(orderId: Long)
    }

    interface Presenter {
        fun fetchOrders()
        fun selectOrder(position: Int)
    }
}
