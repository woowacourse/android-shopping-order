package woowacourse.shopping.ui.orders

import woowacourse.shopping.model.OrderUIModel

interface OrdersContract {
    interface View {
        fun showOrderHistories(orders: List<OrderUIModel>)
        fun navigateToOrderDetail(orderId: Long)
        fun navigateToProductDetail(productId: Int)
    }

    interface Presenter {
        fun getOrders()
        fun navigateToOrderDetail(orderId: Long)
        fun navigateToProductDetail(productId: Int)
    }
}
