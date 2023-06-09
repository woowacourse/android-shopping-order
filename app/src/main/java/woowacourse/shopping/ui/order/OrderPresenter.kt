package woowacourse.shopping.ui.order

import java.util.concurrent.CompletableFuture
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.utils.LogUtil

class OrderPresenter(
    private val view: OrderContract.View,
    private val cartIds: List<Int>,
    private val orderRepository: OrderRepository
) : OrderContract.Presenter {
    override fun fetchOrder() {
        CompletableFuture.supplyAsync {
            orderRepository.getOrder(cartIds)
        }.thenAccept { result ->
            result.onSuccess { order -> view.setOrder(order.toUIModel()) }
                .onFailure { e -> LogUtil.logError(e) }
        }
    }

    override fun processToOrderConfirmation(point: Int) {
        CompletableFuture.supplyAsync {
            orderRepository.postOrder(point, cartIds)
        }.thenAccept { result ->
            result.onSuccess { view.navigateToOrderConfirmation() }
                .onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }
}
