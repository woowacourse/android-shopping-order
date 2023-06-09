package woowacourse.shopping.view.orderdetail

import woowacourse.shopping.model.uimodel.OrderDetailUIModel
import woowacourse.shopping.model.uimodel.OrderProductUIModel

interface OrderDetailContract {
    interface View {
        var presenter: Presenter
        fun updateOrderProducts(orderItems: List<OrderProductUIModel>)
        fun setOrderDetail(orderDetail: OrderDetailUIModel)
    }

    interface Presenter {
        fun setOrderProducts(id: Long)
    }
}
