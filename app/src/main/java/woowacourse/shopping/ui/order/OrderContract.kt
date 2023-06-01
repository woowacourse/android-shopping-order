package woowacourse.shopping.ui.order

import woowacourse.shopping.model.OrderListUIModel

interface OrderContract {
    interface View {
        fun showOrderList(orderListUIModel: OrderListUIModel)
    }

    interface Presenter {
        fun getOrderList()
    }
}
