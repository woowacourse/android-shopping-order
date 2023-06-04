package woowacourse.shopping.presentation.orderlist

import woowacourse.shopping.data.order.OrderRepository
import woowacourse.shopping.model.Order
import woowacourse.shopping.presentation.mapper.toPresentation
import woowacourse.shopping.presentation.model.OrderModel

class OrderListPresenter(
    private val view: OrderListContract.View,
    private val orderRepository: OrderRepository,
) : OrderListContract.Presenter {

    override fun loadOrderList() {
        orderRepository.loadOrders {
            view.showOrderList(it.toPresentation())
        }
    }

    private fun List<Order>.toPresentation(): List<OrderModel> = map { it.toPresentation() }
}
