package woowacourse.shopping.feature.order.detail

import woowacourse.shopping.model.OrderDetailUiModel

interface OrderDetailContract {
    interface View {
        fun showOrderDetail(orderDetail: OrderDetailUiModel)
    }

    interface Presenter {
        fun requestOrderDetail(orderId: Long)
    }
}
