package woowacourse.shopping.feature.userInfo

import woowacourse.shopping.model.OrderUiModel
import woowacourse.shopping.model.PointUiModel

interface UserInfoContract {
    interface View {
        fun showOrders(orders: List<OrderUiModel>)
        fun showPoint(point: PointUiModel)
    }

    interface Presenter {
        fun loadOrders()
        fun loadPoint()
    }
}
