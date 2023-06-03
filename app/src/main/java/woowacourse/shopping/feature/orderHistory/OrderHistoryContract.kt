package woowacourse.shopping.feature.orderHistory

import woowacourse.shopping.model.OrderHistoryProductUiModel

interface OrderHistoryContract {

    interface View {
        fun addOrderHistory(orderHistory: List<OrderHistoryProductUiModel>)
    }

    interface Presenter {
        fun loadOrderHistory()
        fun loadProducts()
    }
}
