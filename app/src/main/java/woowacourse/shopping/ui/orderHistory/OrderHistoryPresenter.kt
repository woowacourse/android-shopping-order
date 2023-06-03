package woowacourse.shopping.ui.orderHistory

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.utils.LogUtil

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    private val orderRepository: OrderRepository,
    private val orderId: Long
) : OrderHistoryContract.Presenter {

    override fun getOrderDetail() {
        orderRepository.getOrderHistory(orderId) { result ->
            result.onSuccess { order -> view.setOrderHistory(order.toUIModel()) }
                .onFailure { e -> LogUtil.logError(e) }
        }
    }
    override fun navigateToProductDetail(productId: Int) {
        view.navigateToProductDetail(productId)
    }
}
