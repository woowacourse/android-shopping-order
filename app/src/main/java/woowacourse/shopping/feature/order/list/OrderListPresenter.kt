package woowacourse.shopping.feature.order.list

import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toPresentation

class OrderListPresenter(
    private val view: OrderListContract.View,
    private val orderRepository: OrderRepository,
) : OrderListContract.Presenter {
    override fun requestOrders() {
        orderRepository.getAll(
            onSuccess = { orderPreviews ->
                view.showOrders(orderPreviews.map { it.toPresentation() })
            },
            onFailure = {
                view.failToLoadOrders(it.message)
            },
        )
    }

    override fun requestOrderDetail(orderId: Long) {
        view.showOrderDetail(orderId)
    }
}
