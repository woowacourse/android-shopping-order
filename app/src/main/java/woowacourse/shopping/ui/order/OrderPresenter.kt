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
    override fun setUpOrder() {
        CompletableFuture.supplyAsync {
            orderRepository.getOrder(cartIds)
        }.thenAccept { result ->
            result.onSuccess { order -> view.showOrder(order.toUIModel()) }
                .onFailure { e -> LogUtil.logError(e) }
        }
    }

    override fun confirmOrder(point: Int) {
        CompletableFuture.supplyAsync {
            orderRepository.postOrder(point, cartIds)
        }.thenAccept { result ->
            result.onSuccess { view.navigateToOrder() }
                .onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }
}
