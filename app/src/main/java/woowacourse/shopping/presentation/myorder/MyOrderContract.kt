package woowacourse.shopping.presentation.myorder

import woowacourse.shopping.presentation.model.OrderModel

interface MyOrderContract {
    interface Presenter {
        fun loadOrders()
    }

    interface View {
        fun setOrders(orderModels: List<OrderModel>)
    }
}
