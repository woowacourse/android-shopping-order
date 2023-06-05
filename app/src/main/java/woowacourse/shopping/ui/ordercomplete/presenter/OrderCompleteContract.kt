package woowacourse.shopping.ui.ordercomplete.presenter

import com.example.domain.model.OrderNumber

interface OrderCompleteContract {
    interface View {
        fun setReceipt(orderNumber: OrderNumber)
    }

    interface Presenter {
        fun getReceipt()
    }
}
