package woowacourse.shopping.presentation.orderdetail

import woowacourse.shopping.data.order.OrderRepository
import woowacourse.shopping.presentation.mapper.toPresentation

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {
    override fun loadOrderInfo(orderId: Long) {
        orderRepository.loadOrder(orderId) {
            view.showOrderInfo(it.toPresentation())
        }
    }
}
