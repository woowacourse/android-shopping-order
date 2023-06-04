package woowacourse.shopping.feature.orderHistory

import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toPresentation

class OrderHistoryPresenter(
    private val view: OrderHistoryContract.View,
    private val orderRepository: OrderRepository
) : OrderHistoryContract.Presenter {

    private var currentPage = 1
    private var totalPage = 0

    override fun loadOrderHistory() {
        orderRepository.getOrderHistory(
            currentPage, callback = {
                it.onSuccess { orderHistoryInfo ->
                    view.addOrderHistory(orderHistoryInfo.info.map { it.toPresentation() })
                    totalPage = orderHistoryInfo.totalPages
                    ++currentPage
                }.onFailure { throwable ->
                    if (currentPage > totalPage) {
                        view.showErrorMessage(Throwable(LAST_PAGE_MESSAGE))
                    } else {
                        view.showErrorMessage(throwable)
                    }
                }
            }
        )
    }

    companion object {
        private const val LAST_PAGE_MESSAGE = "마지막 페이지 입니다"
    }
}
