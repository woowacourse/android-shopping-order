package woowacourse.shopping.ui.orderhistory

import woowacourse.shopping.domain.model.page.LoadMore
import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toUi
import woowacourse.shopping.model.UiOrder
import woowacourse.shopping.ui.orderhistory.OrderHistoryContract.Presenter
import woowacourse.shopping.ui.orderhistory.OrderHistoryContract.View

class OrderHistoryPresenter(
    view: View,
    private val orderRepository: OrderRepository,
    private var page: Page = LoadMore(INITIAL_PAGE, SIZE_PER_PAGE),
) : Presenter(view) {

    override fun loadMoreOrderList() {
        orderRepository.getOrders(
            page = page,
            onSuccess = { orders ->
                view.showExtraOrderList(orders.toUi())
                page = page.next()
            },
            onFailed = { view.showLoadOrderFailed() }
        )
    }

    override fun navigateToHome() {
        view.navigateToHome()
    }

    override fun inquiryOrderDetail(order: UiOrder) {
        view.navigateToOrderDetail(order)
    }

    companion object {
        private const val INITIAL_PAGE = 1
        private const val SIZE_PER_PAGE = 10
    }

}
