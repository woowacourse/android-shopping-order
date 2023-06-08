package woowacourse.shopping.presentation.orderdetail

import woowacourse.shopping.presentation.model.OrderModel

interface OrderDetailContract {
    interface Presenter {
        fun loadOrderInfo(orderId: Long)
    }

    interface View {
        fun showOrderInfo(orderModel: OrderModel)
    }
}
