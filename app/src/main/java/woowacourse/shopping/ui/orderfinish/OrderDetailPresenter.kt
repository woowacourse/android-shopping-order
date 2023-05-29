package woowacourse.shopping.ui.orderfinish

import woowacourse.shopping.data.repository.OrderRepository

class OrderDetailPresenter(
    private val orderRepository: OrderRepository,
    private val orderId: Int,
    private val view: OrderDetailContract.View,
) : OrderDetailContract.Presenter {

    override fun getOrderRecord() {
        orderRepository.getOrderRecord(orderId) {
            view.setUpView(it)
        }
    }
}
