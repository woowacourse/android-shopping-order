package woowacourse.shopping.feature.order

import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toPresentation

class OrderPresenter(
    private val view: OrderContract.View,
    private val orderMockRepository: OrderRepository
) : OrderContract.Presenter {

    private var page = 1

    override fun loadOrders() {
        orderMockRepository.getOrders(
            page = page,
            onSuccess = { orders ->
                view.addOrders(orders.map { it.toPresentation() })
            },
            onFailure = {}
        )
    }

    override fun moveToOrderDetail(orderId: Int) {
        view.showOrderDetailScreen(orderId)
    }
}
