package woowacourse.shopping.ui.order

import woowacourse.shopping.ui.order.uistate.OrderResultUIState

interface OrderResultContract {
    interface Presenter {
        fun onLoadOrder(orderId: Long)
    }
    interface View {
        fun showOrder(order: OrderResultUIState)
    }
}
