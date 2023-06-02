package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.ui.order.recyclerview.ListItem

interface OrderDetailContract {
    interface View {
        fun showOrderDetail(orders: List<ListItem>)
    }

    interface Presenter {
        fun fetchOrderDetail()
    }
}
