package woowacourse.shopping.ui.orderfinish

import woowacourse.shopping.ui.model.Order

interface OrderDetailContract {

    interface View {
        fun setUpView(order: Order)
    }

    interface Presenter {
        fun getOrderRecord()
    }
}
