package woowacourse.shopping.ui.order

interface OrderContract {
    interface View {
        fun showOrderList()
    }

    interface Presenter {
        fun getOrderList()
    }
}
