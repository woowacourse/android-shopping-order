package woowacourse.shopping.feature.orderHistory

import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toPresentation

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    private val orderRepository: OrderRepository
) : OrderHistoryContract.Presenter {

    private var currentPage = 1

    override fun loadOrderHistory() {
        orderRepository.getOrderHistory(
            currentPage, callback = {
                it.onSuccess { orderHistoryInfo ->
                    view.addOrderHistory(orderHistoryInfo.info.map { it.toPresentation() })
                    ++currentPage
                }.onFailure { throwable ->
                    view.showErrorMessage(throwable)
                }
            }
        )
    }
}
