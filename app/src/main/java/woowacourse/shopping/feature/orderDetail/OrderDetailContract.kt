package woowacourse.shopping.feature.orderDetail

import woowacourse.shopping.model.OrderDetailUiModel

interface OrderDetailContract {
    interface View {
        fun showOrderDetail(orderDetail: OrderDetailUiModel)
        fun moveToMainScreen()
        fun showFailureMessage(message: String)
    }

    interface Presenter {
        fun loadOrderDetail(orderId: Int)
        fun cancelOrder()
    }
}
