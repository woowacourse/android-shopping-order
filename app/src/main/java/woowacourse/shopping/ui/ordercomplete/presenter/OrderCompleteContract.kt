package woowacourse.shopping.ui.ordercomplete.presenter

import com.example.domain.model.Receipt

interface OrderCompleteContract {
    interface View {
        fun setReceipt(receipt: Receipt)
    }

    interface Presenter {
        fun getReceipt(orderId: Int)
    }
}
