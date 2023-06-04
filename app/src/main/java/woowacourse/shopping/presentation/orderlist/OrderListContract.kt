package woowacourse.shopping.presentation.orderlist

import woowacourse.shopping.presentation.model.OrderModel

interface OrderListContract {
    interface Presenter {
        fun loadOrderList()
    }

    interface View {
        fun showOrderList(orderModels: List<OrderModel>)
    }
}
