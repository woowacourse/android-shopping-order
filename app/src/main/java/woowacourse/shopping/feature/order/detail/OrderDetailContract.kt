package woowacourse.shopping.feature.order.detail

import woowacourse.shopping.model.OrderDetailUiModel

interface OrderDetailContract {
    interface View {
        fun showOrderDetail(orderDetail: OrderDetailUiModel)
        fun failToLoadOrder(message: String)
    }

    interface Presenter {
        fun requestOrderDetail(orderId: Long)
    }
}
