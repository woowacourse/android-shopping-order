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
    val orderHistories = mutableListOf<OrderHistoryUIModel>()

    override fun getOrderHistories() {
        CompletableFuture.supplyAsync {
            orderRepository.getOrderHistoriesNext()
        }.thenAccept { result ->
            result.onSuccess { histories -> view.showOrderHistories(histories.toUIModel()) }
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
