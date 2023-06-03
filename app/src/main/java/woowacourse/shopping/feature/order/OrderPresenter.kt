package woowacourse.shopping.feature.order

import com.example.domain.model.Order
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toPresentation

class OrderPresenter(
    private val view: OrderContract.View,
    private val orderRepository: OrderRepository
) : OrderContract.Presenter {

    private var orders = mutableListOf<Order>()
    private var page = 1

    override fun loadOrders() {
        orderRepository.getOrders(
            page = page,
            onSuccess = { newOrders ->
                orders.addAll(newOrders)
                ++page
                view.showOrders(orders.map { it.toPresentation() })
            },
            onFailure = {}
        )
    }
}
