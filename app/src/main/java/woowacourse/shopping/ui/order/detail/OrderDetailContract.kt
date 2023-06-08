package woowacourse.shopping.ui.order.detail

import woowacourse.shopping.model.UiOrderedProduct
import woowacourse.shopping.model.UiPayment

interface OrderDetailContract {
    interface View {
        fun navigateToHome()
        fun showOrderDetailPaymentInfo(payment: UiPayment)
        fun showOrderDetailProducts(orderedProducts: List<UiOrderedProduct>)
        fun showLoadFailed(error: String)
    }

    abstract class Presenter(protected val view: View) {
        abstract fun loadOrderDetailInfo()
        abstract fun navigateToHome(itemId: Int)
    }
}
