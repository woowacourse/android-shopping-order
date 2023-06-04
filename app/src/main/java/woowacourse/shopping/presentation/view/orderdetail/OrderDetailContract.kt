package woowacourse.shopping.presentation.view.orderdetail

interface OrderDetailContract {
    interface View {
        val presenter: Presenter

        fun setOrderDateView(date: String)
        fun handleErrorView()
    }

    interface Presenter {
        fun initView()
    }
}
