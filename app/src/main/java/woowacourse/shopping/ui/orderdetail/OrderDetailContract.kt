package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.ui.model.OrderModel

interface OrderDetailContract {
    interface Presenter {
        fun loadDetail(id: Int)
    }

    interface View {
        fun showDetail(order: OrderModel)

        fun notifyLoadFailed()
    }
}