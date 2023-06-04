package woowacourse.shopping.feature.orderdetail

import woowacourse.shopping.model.OrderDetailUiModel

interface OrderDetailContract {
    interface View {
        fun showOrderDetail(orderDetail: OrderDetailUiModel)
    }

    interface Presenter {
        fun requestOrderDetail(orderId: Long)
    }
}
