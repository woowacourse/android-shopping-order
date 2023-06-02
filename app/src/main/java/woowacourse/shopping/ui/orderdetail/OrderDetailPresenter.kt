package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.model.OrderModel
import woowacourse.shopping.ui.orderdetail.OrderDetailContract.Presenter
import woowacourse.shopping.ui.orderdetail.OrderDetailContract.View

class OrderDetailPresenter(
    private val view: View,
    private val order: OrderModel,
) : Presenter {
    override fun fetchOrderDetail() {
        view.showOrderDetail(order.orderProducts + order.payment)
    }
}
