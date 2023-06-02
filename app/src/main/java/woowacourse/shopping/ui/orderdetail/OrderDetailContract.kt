package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.ui.order.recyclerview.ListItem

interface OrderDetailContract {
    interface View {
        fun showOrderDetail(orders: List<ListItem>)
        fun navigateToOrderHistory()
    }

    interface Presenter {
        fun fetchOrderDetail()
        fun navigateToOrderHistory()
    }
}
