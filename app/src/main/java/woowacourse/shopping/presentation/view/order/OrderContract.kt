package woowacourse.shopping.presentation.view.order

interface OrderContract {
    interface View {
        val presenter: Presenter
    }

    interface Presenter
}
