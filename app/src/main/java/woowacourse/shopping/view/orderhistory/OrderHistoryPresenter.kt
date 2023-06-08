package woowacourse.shopping.view.orderhistory

import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.model.toUiModel

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    private val orderRepository: OrderRepository,
) : OrderHistoryContract.Presenter {
    override fun fetchOrders() {
        orderRepository.getAll { result ->
            when (result) {
                is DataResult.Success -> {
                    val orders = result.response.map { it.toUiModel() }
                    view.showOrders(orders)
                }
                is DataResult.Failure -> {
                    view.showServerFailureToast()
                }
                is DataResult.NotSuccessfulError -> {
                    view.showNotSuccessfulErrorToast()
                }
                is DataResult.WrongResponse -> {
                    view.showServerResponseWrongToast()
                }
            }
        }
    }
}
