package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.ui.mapper.toOrderUiModel

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    private val repository: OrderRepository,
) : OrderHistoryContract.Presenter {

    override fun getOrders() {
        repository.getOrders {
            view.initView(
                it.map { order ->
                    order.toOrderUiModel()
                }
            )
        }
    }
}
