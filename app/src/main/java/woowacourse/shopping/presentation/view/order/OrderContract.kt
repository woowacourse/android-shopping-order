package woowacourse.shopping.presentation.view.order

interface OrderContract {
    interface View {
        val presenter: Presenter

        fun setAvailablePointView(point: Int)
        fun setSavingPoint(point: Int)
        fun handleErrorView()
    }

    interface Presenter {
        fun initReservedPoint()
        fun initSavingPoint()
    }
}
