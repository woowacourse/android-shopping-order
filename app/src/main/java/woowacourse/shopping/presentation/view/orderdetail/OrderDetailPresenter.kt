package woowacourse.shopping.presentation.view.orderdetail

import woowacourse.shopping.data.mapper.toUIModel
import woowacourse.shopping.data.respository.order.OrderRepository
import woowacourse.shopping.presentation.model.CartModel

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderId: Long,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {

    private lateinit var orderProducts: List<CartModel>

    override fun initView() {
        orderRepository.requestOrder(orderId, ::onFailure) { orderDetailEntity ->
            orderProducts = orderDetailEntity.products.map { it.toUIModel() }
            view.setView(orderDetailEntity, orderProducts)
        }
    }

    private fun onFailure() {
        view.handleErrorView()
    }
}
