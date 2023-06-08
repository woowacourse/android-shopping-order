package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.mapper.toUi

class OrderHistoryPresenter(
    override val view: OrderHistoryContract.View,
    private val orderRepository: OrderRepository
) : OrderHistoryContract.Presenter {

    init {
        fetchOrdersInfo()
    }

    private fun fetchOrdersInfo() {
        orderRepository.getOrdersInfo { ordersInfo ->
            view.updateOrdersInfo(ordersInfo.map { it.toUi() })
        }
    }
}
