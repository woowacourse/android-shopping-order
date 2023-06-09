package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.uimodel.OrderHistoryUIModel
import woowacourse.shopping.uimodel.OrderProductUIModel

interface OrderDetailContract {
    interface View {
        fun setOrderList(orderProducts: List<OrderProductUIModel>)
        fun setOrderHistory(orderHistory: OrderHistoryUIModel)
    }

    interface Presenter {
        fun gerOrderProducts(id: Int)
    }
}
