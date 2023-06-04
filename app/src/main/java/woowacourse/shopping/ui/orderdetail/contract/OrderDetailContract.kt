package woowacourse.shopping.ui.orderdetail.contract

import woowacourse.shopping.model.OrderUIModel

interface OrderDetailContract {
    interface View {
        fun setOrderDetail(order: OrderUIModel)
    }

    interface Presenter {
        fun getOrderDetail(id: Long)
    }
}
