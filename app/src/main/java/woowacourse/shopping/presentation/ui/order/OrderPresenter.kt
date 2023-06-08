package woowacourse.shopping.presentation.ui.order

import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.model.OrderUiModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val orderRepository: OrderRepository,
) : OrderContract.Presenter {
    private var orders: List<Order> = emptyList()

    override fun fetchOrders() {
        orderRepository.fetchOrders { result ->
            result
                .onSuccess { view.showOrders(it.toUiModel()) }
                .onFailure { view.showError(it.message ?: "에러 메시지가 없습니다.") }
        }
    }

    private fun List<Order>.toUiModel(): List<OrderUiModel> {
        return this.map {
            OrderUiModel(
                name = it.products.first().product.name,
                image = it.products.first().product.imageUrl,
                otherCount = it.products.size - 1,
                dateTime = it.orderedDateTime,
                price = it.totalPrice,
            )
        }
    }

    override fun selectOrder(position: Int) {
        view.showOrderDetail(orders[position].orderId)
    }
}
