package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.ui.model.Order

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
    private val orderId: Int,
    private val order: Order?,
) : OrderDetailContract.Presenter {

    override fun getOrder() {
        if (orderId != -1) {
            orderRepository.getOrder(orderId) {
                view.initView(it)
            }
        }
        order?.let {
            view.initView(it)
        }
    }
}
