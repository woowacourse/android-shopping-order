package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.data.repository.OrderRepository

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    private val repository: OrderRepository,
) : OrderHistoryContract.Presenter {

    override fun getOrders() {
        repository.getOrders(
            onReceived = view::setUpView
        )
    }
}
