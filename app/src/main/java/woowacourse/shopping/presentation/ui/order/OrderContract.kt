package woowacourse.shopping.presentation.ui.order

import woowacourse.shopping.presentation.model.OrderUiModel

interface OrderContract {
    interface View {
        fun showOrders(orders: List<OrderUiModel>)
        fun showError(message: String)
        fun showOrderDetail(orderId: Long)
    }

    interface Presenter {
        fun fetchOrders()
        fun selectOrder(position: Int)
    }
}
