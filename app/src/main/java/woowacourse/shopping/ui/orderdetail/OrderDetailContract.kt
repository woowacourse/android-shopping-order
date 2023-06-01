package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.ui.order.recyclerview.ListItem

interface OrderDetailContract {
    interface View {
        fun showOrderDetail(orders: List<ListItem>)
    }

    abstract class Presenter(protected val view: View) {
        abstract fun fetchOrderDetail()
    }
}
