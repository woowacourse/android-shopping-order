package woowacourse.shopping.ui.orderHistories

import java.util.concurrent.CompletableFuture
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.OrderHistoryUIModel
import woowacourse.shopping.utils.LogUtil

class OrderHistoriesPresenter(
    private val view: OrderHistoriesContract.View,
    private val orderRepository: OrderRepository
) : OrderHistoriesContract.Presenter {
    private val histories = mutableListOf<OrderHistoryUIModel>()
    private var lastOrderId: Long = 0

    override fun fetchOrderHistories() {
        if (lastOrderId == -1L) return

        CompletableFuture.supplyAsync { orderRepository.getOrderHistoriesNext(lastOrderId) }.get()
            .onSuccess { nextHistories ->
                if (nextHistories.orderHistories.isEmpty()) {
                    lastOrderId = -1
                    return@onSuccess
                }
                histories.addAll(nextHistories.toUIModel().orderHistories)
                lastOrderId = nextHistories.lastOrderId
                view.showOrderHistories(histories)
            }
            .onFailure { e -> LogUtil.logError(e) }
    }

    override fun navigateToOrderHistory(orderId: Long) {
        view.navigateToOrderHistory(orderId)
    }

    override fun navigateToProductDetail(productId: Int) {
        view.navigateToProductDetail(productId)
    }
}
