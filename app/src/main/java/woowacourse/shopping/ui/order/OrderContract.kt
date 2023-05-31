package woowacourse.shopping.ui.order

import woowacourse.shopping.model.UiPoint
import woowacourse.shopping.model.UiPrice
import woowacourse.shopping.ui.order.recyclerview.ListItem

interface OrderContract {
    interface View {
        fun updateOrder(orderItems: List<ListItem>)
        fun showOrderCompleted()
        fun showOrderFailed()
        fun navigateToHome()
        fun showOrderLoadFailed()
        fun showTotalPayment(totalPayment: UiPrice)
    }

    interface Presenter {
        fun fetchAll()
        fun order()
        fun applyPoint(point: UiPoint)
    }
}
