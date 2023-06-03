package woowacourse.shopping.view.orderdetail

import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.model.OrderDetailModel

class OrderDetailPresenter(
    private val orderId: Int,
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {
    override fun fetchOrder() {
        orderRepository.getOrder(orderId) {
            val model = OrderDetailModel.from(it)
            view.showOrderDetail(model)
        }
    }
}
