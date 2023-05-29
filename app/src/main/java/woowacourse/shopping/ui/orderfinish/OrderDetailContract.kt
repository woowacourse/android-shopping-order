package woowacourse.shopping.ui.orderfinish

import woowacourse.shopping.ui.model.OrderRecord

interface OrderDetailContract {

    interface View {
        fun setUpView(orderRecord: OrderRecord)
    }

    interface Presenter {
        fun getOrderRecord()
    }
}
