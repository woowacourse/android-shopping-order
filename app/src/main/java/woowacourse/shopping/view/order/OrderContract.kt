package woowacourse.shopping.view.order

import woowacourse.shopping.model.OrderModel

interface OrderContract {
    interface View {
        fun showOrder(orderModel: OrderModel)
        fun showUnableToast()
        fun showOrderComplete(orderId: Int)
    }
    interface Presenter {
        fun fetchOrder()
        fun order()
    }
}
