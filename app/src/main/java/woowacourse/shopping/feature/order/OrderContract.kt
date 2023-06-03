package woowacourse.shopping.feature.order

import woowacourse.shopping.model.OrderUiModel

interface OrderContract {
    interface View {
        fun showOrders(orders: List<OrderUiModel>)
    }

    interface Presenter {
        fun loadOrders()
    }
}
