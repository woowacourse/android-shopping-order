package woowacourse.shopping.ui.order

interface OrderContract {
    interface View
    interface Presenter {
        fun getOrderInfo(ids: List<Int>)
    }
}
