package woowacourse.shopping.ui.orders

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.utils.LogUtil

class OrdersPresenter(
    private val view: OrdersContract.View,
    private val orderRepository: OrderRepository
) : OrdersContract.Presenter {
    override fun getOrders() {
        orderRepository.getOrders { result ->
            result.onSuccess { orders -> view.showOrderHistories(orders.toUIModel()) }
                .onFailure { e -> LogUtil.logError(e) }
        }
    }

    override fun navigateToOrderDetail(orderId: Long) {
        view.navigateToOrderDetail(orderId)
    }

    override fun navigateToProductDetail(productId: Int) {
        view.navigateToProductDetail(productId)
    }
}
