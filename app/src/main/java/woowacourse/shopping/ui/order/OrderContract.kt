package woowacourse.shopping.ui.order

import woowacourse.shopping.model.PointModel
import woowacourse.shopping.model.PriceModel
import woowacourse.shopping.ui.order.recyclerview.ListItem

interface OrderContract {
    interface View {
        fun updateOrder(orderItems: List<ListItem>)
        fun showOrderLoadFailed()
        fun showOrderCompleted()
        fun showOrderFailed()
        fun showFinalPayment(totalPayment: PriceModel)
        fun navigateToHome()
    }

    interface Presenter {
        fun fetchAll()
        fun applyPoint(point: PointModel)
        fun order()
        fun navigateToHome()
    }
}
