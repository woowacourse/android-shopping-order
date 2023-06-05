package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.mapper.toUiModel

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    private val repository: OrderRepository,
) : OrderHistoryContract.Presenter {

    override fun getOrders() {
        repository.getOrders()
            .thenAccept {
                val orders = it.getOrThrow().map { order ->
                    order.toUiModel()
                }

                view.initView(orders)
            }
            .exceptionally { error ->
                error.message?.let { view.showErrorMessage(it) }
                null
            }
    }
}
