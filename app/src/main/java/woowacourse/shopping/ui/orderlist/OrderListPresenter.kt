package woowacourse.shopping.ui.orderlist

import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.utils.ActivityUtils.showErrorMessage

class OrderListPresenter(
    private val view: OrderListContract.View,
    private val orderRepository: OrderRepository,
) : OrderListContract.Presenter {

    override fun getOrderList() {
        orderRepository.getOrderHistoryList(
            { view.setOrderList(it.map(OrderHistory::toUIModel)) },
            { showErrorMessage(it.message) },
        )
    }

    override fun showOrderDetail(orderId: Int) {
        view.navigateToOrderDetail(orderId)
    }
}
