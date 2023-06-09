package woowacourse.shopping.presentation.view.orderlist

import woowacourse.shopping.data.mapper.toUiModel
import woowacourse.shopping.data.respository.order.OrderRepository
import woowacourse.shopping.presentation.model.OrderDetailModel

class OrderListPresenter(
    private val view: OrderListContract.View,
    private val orderRepository: OrderRepository,
) : OrderListContract.Presenter {

    private lateinit var orders: List<OrderDetailModel>

    override fun initOrders() {
        orderRepository.requestOrders(::onFailure) { orderDetailEntities ->
            orders = orderDetailEntities.map { it.toUiModel() }
            view.setOrderView(orders)
        }
    }

    private fun onFailure() {
        view.handleErrorView()
    }
}
