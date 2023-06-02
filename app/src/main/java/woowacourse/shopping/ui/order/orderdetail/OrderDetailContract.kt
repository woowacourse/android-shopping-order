package woowacourse.shopping.ui.order.orderdetail

import woowacourse.shopping.ui.ErrorView
import woowacourse.shopping.ui.order.uistate.OrderUIState
import woowacourse.shopping.ui.order.uistate.PaymentUIState

interface OrderDetailContract {
    interface Presenter {
        fun loadOrder(orderId: Long)
    }

    interface View : ErrorView {
        fun showOrder(order: OrderUIState, payment: PaymentUIState)
        override fun showError(message: String)
    }
}
