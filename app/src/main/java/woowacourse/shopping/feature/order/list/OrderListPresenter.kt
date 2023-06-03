package woowacourse.shopping.feature.order.list

import com.example.domain.model.OrderMinInfoItem
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toPresentation

class OrderListPresenter(
    private val view: OrderListContract.View,
    private val orderRepository: OrderRepository,
) : OrderListContract.Presenter {
    override fun loadOrderItems() {
        orderRepository.fetchAllOrders(
            onSuccess = { orders ->
                view.setOrderListItems(orders.map(OrderMinInfoItem::toPresentation))
            },
            onFailure = {}
        )
    }

    override fun requestOrderDetail(orderId: Long) {
        view.showOrderDetail(orderId)
    }
}
