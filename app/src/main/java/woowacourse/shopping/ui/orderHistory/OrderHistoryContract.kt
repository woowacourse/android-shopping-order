package woowacourse.shopping.ui.orderHistory

import woowacourse.shopping.model.OrderHistoryUIModel

interface OrderHistoryContract {
    interface View {
        fun setOrderHistory(orderHistory: OrderHistoryUIModel)
        fun navigateToProductDetail(productId: Int)
    }

    interface Presenter {
        fun fetchOrderDetail()
        fun navigateToProductDetail(productId: Int)
    }
}
