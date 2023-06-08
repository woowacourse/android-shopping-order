package woowacourse.shopping.ui.orderHistories

import woowacourse.shopping.model.OrderHistoryUIModel

interface OrderHistoriesContract {
    interface View {
        fun setOrderHistories(orderHistories: List<OrderHistoryUIModel>)
        fun navigateToOrderHistory(orderId: Long)
        fun navigateToProductDetail(productId: Int)
    }

    interface Presenter {
        fun fetchOrderHistories()
        fun processToOrderHistory(orderId: Long)
        fun processToProductDetail(productId: Int)
    }
}