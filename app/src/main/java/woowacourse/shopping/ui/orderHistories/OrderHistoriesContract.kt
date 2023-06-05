package woowacourse.shopping.ui.orderHistories

import woowacourse.shopping.model.OrderHistoryUIModel

interface OrderHistoriesContract {
    interface View {
        fun showOrderHistories(orderHistories: List<OrderHistoryUIModel>)
        fun navigateToOrderHistory(orderId: Long)
        fun navigateToProductDetail(productId: Int)
    }

    interface Presenter {
        fun fetchOrderHistories()
        fun navigateToOrderHistory(orderId: Long)
        fun navigateToProductDetail(productId: Int)
    }
}
