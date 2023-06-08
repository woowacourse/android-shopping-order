package woowacourse.shopping.ui.order.history

import android.util.Log
import woowacourse.shopping.data.mapper.toUiModel
import woowacourse.shopping.domain.model.OrderResponse
import woowacourse.shopping.domain.model.page.LoadMore
import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.domain.repository.OrderProductRepository
import woowacourse.shopping.ui.order.history.OrderHistoryContract.Presenter
import woowacourse.shopping.ui.order.history.OrderHistoryContract.View
import woowacourse.shopping.util.collection.DistinctList

class OrderHistoryPresenter(
    view: View,
    private val orderProductRepository: OrderProductRepository,
    private var page: Page = LoadMore(INITIAL_PAGE, SIZE_PER_PAGE),
) : Presenter(view) {
    private val orders: DistinctList<OrderResponse> = DistinctList()

    override fun loadOrderedProducts() {
        orderProductRepository.requestOrders(
            page = page,
            onSuccess = { fetchedOrders ->
                orders.addAll(fetchedOrders)
                view.showOrderedProducts(orders.map { it.toUiModel() })
                page = page.next()
            },
            onFailure = { errorMessage ->
                Log.d("error", "[ERROR] 데이터를 불러오는 데에 실패했습니다. : $errorMessage")
                view.showLoadFailed(errorMessage)
            },
        )
    }

    override fun navigateToHome(itemId: Int) {
        when (itemId) {
            android.R.id.home -> {
                view.navigateToHome()
            }
        }
    }

    companion object {
        private const val INITIAL_PAGE = 1
        private const val SIZE_PER_PAGE = 10
    }
}
