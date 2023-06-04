package woowacourse.shopping.presentation.view.orderdetail

import woowacourse.shopping.data.mapper.toUIModel
import woowacourse.shopping.data.respository.order.OrderRepository

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderId: Long,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {

    override fun initView() {
        orderRepository.requestOrder(orderId, ::onFailure) { orderDetailEntity ->
            val orderProducts = orderDetailEntity.products.map { it.toUIModel() }
            view.setOrderDateView(orderDetailEntity.orderedAt)
            view.setOrderProductsView(orderProducts)
        }
    }

    private fun onFailure() {
        view.handleErrorView()
    }
}
