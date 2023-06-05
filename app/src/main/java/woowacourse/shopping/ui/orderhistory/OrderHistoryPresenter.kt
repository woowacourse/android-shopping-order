package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.mapper.toUiModel

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    private val repository: OrderRepository,
) : OrderHistoryContract.Presenter {

    override fun getOrders() {
        repository.getOrders(
            onReceived = { orders ->
                view.initView(orders.map { it.toUiModel() })
            },
            onFailed = { errorMessage ->
                view.showErrorMessage(errorMessage)
            }
        )
    }
}
