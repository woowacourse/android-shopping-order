package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.page.LoadMore
import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.model.OrderModel
import woowacourse.shopping.model.mapper.toUi
import woowacourse.shopping.ui.orderhistory.OrderHistoryContract.Presenter
import woowacourse.shopping.ui.orderhistory.OrderHistoryContract.View

class OrderHistoryPresenter(
    private val view: View,
    private val orderRepository: OrderRepository,
    private var page: Page = LoadMore(INITIAL_PAGE, SIZE_PER_PAGE),
) : Presenter {
    private val orders: MutableList<Order> = mutableListOf()

    override fun loadMoreOrders() {
        view.showLoading()
        orderRepository.getOrders(
            page = page.getPageForCheckHasNext(),
            onSuccess = { fetchedOrders ->
                orders.addAll(fetchedOrders)
                view.showOrders(orders.toUi())
                view.hideLoading()
                page = page.next()
            },
            onFailed = { view.hideLoading() }
        )
    }

    override fun navigateToHome() {
        view.navigateToHome()
    }

    override fun inquiryOrderDetail(order: OrderModel) {
        view.navigateToOrderDetail(order)
    }

    companion object {
        private const val INITIAL_PAGE = 1
        private const val SIZE_PER_PAGE = 10
    }

}
