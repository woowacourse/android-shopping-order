package woowacourse.shopping.view.orderdetail

import woowacourse.shopping.model.OrderDetailModel

interface OrderDetailContract {
    interface View {
        fun showOrderDetail(orderDetailModel: OrderDetailModel)

        fun showErrorMessageToast(message: String?)
    }

    interface Presenter {
        fun fetchOrder()
    }
}
