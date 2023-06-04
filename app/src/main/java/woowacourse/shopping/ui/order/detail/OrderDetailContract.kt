package woowacourse.shopping.ui.order.detail

interface OrderDetailContract {
    interface View {
        fun navigateToHome()
    }

    abstract class Presenter(protected val view: View) {
        abstract fun navigateToHome(itemId: Int)
    }
}
