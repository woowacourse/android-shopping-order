package woowacourse.shopping.ui.order.history

import woowacourse.shopping.model.UiOrderResponse

interface OrderHistoryContract {
    interface View {
        fun navigateToHome()
        fun showOrderedProducts(orderedProducts: List<UiOrderResponse>)
    }

    abstract class Presenter(protected val view: View) {
        abstract fun loadOrderedProducts()
        abstract fun navigateToHome(itemId: Int)
    }
}
