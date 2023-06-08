package woowacourse.shopping.ui.ordercomplete.presenter

import woowacourse.shopping.ui.ordercomplete.uimodel.Bill

interface OrderCompleteContract {
    interface View {
        fun setReceipt(bill: Bill)
    }

    interface Presenter {
        fun getReceipt(orderId: Int)
    }
}
