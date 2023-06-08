package woowacourse.shopping.presentation.orderdetail

import woowacourse.shopping.presentation.model.OrderDetailModel

interface OrderDetailContract {
    interface View {
        fun showOrderDetail(orderDetail: OrderDetailModel)
    }

    interface Presenter {
        fun loadOrderDetail(orderId: Int)
    }
}
