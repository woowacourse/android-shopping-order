package woowacourse.shopping.ui.order

import woowacourse.shopping.model.OrderUIModel

interface OrderContract {
    interface View {
        fun showOrder(order: OrderUIModel)
        fun navigateOrder(orderId: Long)
    }

    interface Presenter {
        fun getOrder()
        fun confirmOrder(point: Int)
    }
}
