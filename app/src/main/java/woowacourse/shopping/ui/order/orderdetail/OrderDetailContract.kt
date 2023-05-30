package woowacourse.shopping.ui.order.orderdetail

import woowacourse.shopping.ui.order.uistate.OrderUIState

interface OrderDetailContract {
    interface Presenter {
        fun loadOrder(orderId: Long)
    }

    interface View {
        fun showOrder(order: OrderUIState)
    }
}
