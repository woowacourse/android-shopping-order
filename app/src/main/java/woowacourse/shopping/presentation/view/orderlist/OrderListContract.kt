package woowacourse.shopping.presentation.view.orderlist

import woowacourse.shopping.presentation.model.OrderDetailModel

interface OrderListContract {
    interface View {
        val presenter: Presenter

        fun setOrderView(orders: List<OrderDetailModel>)
        fun handleErrorView()
    }

    interface Presenter {
        fun initOrders()
    }
}
