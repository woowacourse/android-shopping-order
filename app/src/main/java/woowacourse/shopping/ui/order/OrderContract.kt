package woowacourse.shopping.ui.order

import woowacourse.shopping.model.OrderUIModel

interface OrderContract {
    interface View {
        fun showOrderList(orderUIModel: OrderUIModel)
        fun navigateOrder(orderId: Long)
    }

    interface Presenter {
        fun getOrderList()
        fun confirmOrder(point: Int)
    }
}
