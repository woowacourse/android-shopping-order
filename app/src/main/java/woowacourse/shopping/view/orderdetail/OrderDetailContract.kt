package woowacourse.shopping.view.orderdetail

import woowacourse.shopping.model.OrderDetailModel

interface OrderDetailContract {
    interface View {
        fun showOrderDetail(orderDetailModel: OrderDetailModel)
    }
    interface Presenter {
        fun fetchOrder()
    }
}
