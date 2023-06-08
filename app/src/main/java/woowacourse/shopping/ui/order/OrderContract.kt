package woowacourse.shopping.ui.order

import woowacourse.shopping.model.OrderUIModel

interface OrderContract {
    interface View {
        fun setOrder(order: OrderUIModel)
        fun navigateToOrderConfirmation()
    }

    interface Presenter {
        fun fetchOrder()
        fun processToOrderConfirmation(point: Int)
    }
}
