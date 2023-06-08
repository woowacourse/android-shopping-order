package woowacourse.shopping.view.orderdetail

import woowacourse.shopping.model.OrderDetailModel

interface OrderDetailContract {
    interface View {
        fun showOrderDetail(orderDetailModel: OrderDetailModel)
        fun showNotSuccessfulErrorToast()
        fun showServerFailureToast()
        fun showServerResponseWrongToast()
    }

    interface Presenter {
        fun fetchOrder()
    }
}
