package woowacourse.shopping.ui.orderHistories

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.utils.LogUtil

class OrderHistoriesPresenter(
    private val view: OrderHistoriesContract.View,
    private val orderRepository: OrderRepository
) : OrderHistoriesContract.Presenter {
    override fun getOrderHistories() {
        orderRepository.getOrders { result ->
            result.onSuccess { orders -> view.showOrderHistories(orders.toUIModel()) }
                .onFailure { e -> LogUtil.logError(e) }
        }
    }

    override fun navigateToOrderHistory(orderId: Long) {
        view.navigateToOrderHistory(orderId)
    }

    override fun navigateToProductDetail(productId: Int) {
        view.navigateToProductDetail(productId)
    }
}
