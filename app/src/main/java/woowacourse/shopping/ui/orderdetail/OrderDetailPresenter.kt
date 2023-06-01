package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.model.OrderModel
import woowacourse.shopping.ui.orderdetail.OrderDetailContract.Presenter
import woowacourse.shopping.ui.orderdetail.OrderDetailContract.View

class OrderDetailPresenter(
    view: View,
    private val order: OrderModel,
) : Presenter(view) {
    override fun fetchOrderDetail() {
        view.showOrderDetail(order.orderProducts + order.payment)
    }
}
