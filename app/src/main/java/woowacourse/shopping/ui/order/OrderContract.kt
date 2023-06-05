package woowacourse.shopping.ui.order

import woowacourse.shopping.model.OrderUIModel

interface OrderContract {
    interface View {
        fun showOrder(order: OrderUIModel)
        fun navigatetoOrder()
    }

    interface Presenter {
        fun setUpOrder()
        fun confirmOrder(point: Int)
    }
}
