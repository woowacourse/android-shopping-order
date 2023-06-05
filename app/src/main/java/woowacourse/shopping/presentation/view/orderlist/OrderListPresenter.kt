package woowacourse.shopping.presentation.view.orderlist

import woowacourse.shopping.presentation.mapper.toUIModel
import woowacouse.shopping.data.repository.order.OrderRepository

class OrderListPresenter(
    private val view: OrderListContract.View,
    private val orderRepository: OrderRepository,
) : OrderListContract.Presenter {
    override fun loadOrderList() {
        orderRepository.loadOrderList(::onFailure) { orders ->
            view.showOrderListItemView(orders.map { it.toUIModel() })
        }
    }

    private fun onFailure(message: String) {
        view.handleErrorView(message)
    }
}
