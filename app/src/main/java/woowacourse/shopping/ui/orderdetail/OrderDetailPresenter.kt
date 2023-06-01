package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.model.UiOrder
import woowacourse.shopping.ui.orderdetail.OrderDetailContract.Presenter
import woowacourse.shopping.ui.orderdetail.OrderDetailContract.View

class OrderDetailPresenter(
    view: View,
    private val order: UiOrder,
) : Presenter(view) {
    override fun fetchOrderDetail() {
        view.showOrderDetail(order.orderProducts + order.payment)
    }
}
