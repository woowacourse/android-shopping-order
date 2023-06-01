package woowacourse.shopping.presentation.myorder

import woowacourse.shopping.presentation.model.OrderModel

interface MyOrderContract {
    interface Presenter {
        fun loadOrders()
        fun showOrderDetail(orderModel: OrderModel)
    }

    interface View {
        fun setOrders(orderModels: List<OrderModel>)
        fun navigateToOrderDetail(orderId: Int)
    }
}
