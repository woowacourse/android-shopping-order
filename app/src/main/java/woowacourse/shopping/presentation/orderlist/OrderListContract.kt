package woowacourse.shopping.presentation.orderlist

import woowacourse.shopping.presentation.model.OrderModel

interface OrderListContract {
    interface View {
        fun showOrderList(orders: List<OrderModel>)
    }

    interface Presenter {
        fun loadOrderList()
    }
}
