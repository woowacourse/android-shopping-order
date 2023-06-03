package woowacourse.shopping.ui.orderDetail

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.utils.LogUtil

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
    private val orderId: Long
) : OrderDetailContract.Presenter {

    override fun getOrderDetail() {
        orderRepository.getOrder(orderId) { result ->
            result.onSuccess { order -> view.setOrder(order.toUIModel()) }
                .onFailure { e -> LogUtil.logError(e) }
        }
    }
    override fun navigateToProductDetail(productId: Int) {
        view.navigateToProductDetail(productId)
    }
}
