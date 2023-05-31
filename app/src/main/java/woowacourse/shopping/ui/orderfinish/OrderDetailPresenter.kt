package woowacourse.shopping.ui.orderfinish

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.ui.model.Order

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
    private val orderId: Int,
    private val order: Order?,
) : OrderDetailContract.Presenter {

    override fun getOrderRecord() {
        if (orderId != -1) {
            orderRepository.getOrder(orderId) {
                view.setUpView(it)
            }
        }
        order?.let {
            view.setUpView(it)
        }
    }
}
