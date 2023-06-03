package woowacourse.shopping.feature.order

import woowacourse.shopping.model.OrderUiModel
import woowacourse.shopping.model.PointUiModel

interface OrderContract {
    interface View {
        fun showOrders(orders: List<OrderUiModel>)
        fun showPoint(point: PointUiModel)
    }

    interface Presenter {
        fun loadOrders()
        fun loadPoint()
    }
}
