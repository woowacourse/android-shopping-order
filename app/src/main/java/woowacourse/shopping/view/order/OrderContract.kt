package woowacourse.shopping.view.order

import woowacourse.shopping.model.OrderUserInfoModel

interface OrderContract {
    interface View {
        fun showOrder(orderUserInfoModel: OrderUserInfoModel)
        fun showUnableToast()
        fun showOrderComplete(orderId: Int)
        fun showNotSuccessfulErrorToast()
        fun showServerFailureToast()
    }
    interface Presenter {
        fun fetchOrder()
        fun order()
    }
}
