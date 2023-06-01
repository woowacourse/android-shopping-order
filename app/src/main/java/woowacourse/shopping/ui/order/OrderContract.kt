package woowacourse.shopping.ui.order

import woowacourse.shopping.model.UiPoint
import woowacourse.shopping.model.UiPrice
import woowacourse.shopping.ui.order.recyclerview.ListItem

interface OrderContract {
    interface View {
        fun updateOrder(orderItems: List<ListItem>)
        fun showOrderLoadFailed()
        fun showOrderCompleted()
        fun showOrderFailed()
        fun showFinalPayment(totalPayment: UiPrice)
        fun navigateToHome()
    }

    interface Presenter {
        fun fetchAll()
        fun applyPoint(point: UiPoint)
        fun order()
        fun navigateToHome()
    }
}
