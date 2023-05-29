package woowacourse.shopping.feature.order

import woowacourse.shopping.model.OrderUiModel

interface OrderContract {
    interface View {
        fun showOrders(orders: List<OrderUiModel>)
        fun showOrderDetailScreen(orderId: Int)
    }

    interface Presenter {
        fun loadOrders()
        fun moveToOrderDetail(orderId: Int)
    }
}
