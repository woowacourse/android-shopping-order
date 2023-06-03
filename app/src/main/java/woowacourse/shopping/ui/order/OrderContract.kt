package woowacourse.shopping.ui.order

import woowacourse.shopping.model.OrderListUIModel

interface OrderContract {
    interface View {
        fun showOrderList(orderListUIModel: OrderListUIModel)
        fun navigateOrder(orderId: Long)
    }

    interface Presenter {
        fun getOrderList()
        fun confirmOrder(point: Int)
    }
}
