package woowacourse.shopping.ui.order.history

interface OrderHistoryContract {
    interface View

    abstract class Presenter(protected val view: View)
}
