package woowacourse.shopping.ui.order

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.utils.LogUtil

class OrderPresenter(
    private val view: OrderContract.View,
    private val cartIds: List<Int>,
    private val orderRepository: OrderRepository
) : OrderContract.Presenter {
    override fun getOrder() {
        orderRepository.getOrderList(cartIds) { result ->
            result.onSuccess { orderList -> view.showOrder(orderList.toUIModel()) }
                .onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    override fun confirmOrder(point: Int) {
        orderRepository.postOrder(point, cartIds) { result ->
            result.onSuccess { orderId -> view.navigateOrder(orderId) }
                .onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }
}
