package woowacourse.shopping.ui.orderDetail

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.utils.LogUtil

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
    orderId: Long
) : OrderDetailContract.Presenter {

    init {
        getOrderDetail(orderId)
    }

    override fun getOrderDetail(id: Long) {
        orderRepository.getOrder(id) { result ->
            result.onSuccess { order -> view.setOrder(order.toUIModel()) }
                .onFailure { e -> LogUtil.logError(e) }
        }
    }
    override fun navigateToProductDetail(productId: Int) {
        view.navigateToProductDetail(productId)
    }
}
