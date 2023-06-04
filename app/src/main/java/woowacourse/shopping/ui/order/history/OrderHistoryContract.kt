package woowacourse.shopping.ui.order.history

interface OrderHistoryContract {
    interface View {
        fun navigateToHome()
    }

    abstract class Presenter(protected val view: View) {
        abstract fun navigateToHome(itemId: Int)
    }
}
