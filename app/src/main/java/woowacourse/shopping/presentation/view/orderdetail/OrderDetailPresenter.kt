package woowacourse.shopping.presentation.view.orderdetail

import woowacourse.shopping.data.respository.order.OrderRepository

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderId: Long,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {

    override fun initView() {
        orderRepository.requestOrder(orderId, ::onFailure) { orderDetailEntity ->
            view.setOrderDateView(orderDetailEntity.orderedAt)
        }
    }

    private fun onFailure() {
        view.handleErrorView()
    }
}
