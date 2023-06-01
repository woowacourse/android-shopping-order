package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.ui.model.Order

interface OrderDetailContract {

    interface View {
        fun initView(order: Order)
    }

    interface Presenter {
        fun getOrder()
    }
}
