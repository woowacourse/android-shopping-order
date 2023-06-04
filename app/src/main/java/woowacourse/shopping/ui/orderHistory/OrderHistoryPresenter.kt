package woowacourse.shopping.ui.orderHistory

import java.util.concurrent.CompletableFuture
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.utils.LogUtil

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    private val orderRepository: OrderRepository,
    private val orderId: Long
) : OrderHistoryContract.Presenter {

    override fun getOrderDetail() {
        CompletableFuture.supplyAsync {
            orderRepository.getOrderHistory(orderId)
        }.thenAccept { result ->
            result.onSuccess { order -> view.setOrderHistory(order.toUIModel()) }
                .onFailure { e -> LogUtil.logError(e) }
        }
    }
    override fun navigateToProductDetail(productId: Int) {
        view.navigateToProductDetail(productId)
    }
}
