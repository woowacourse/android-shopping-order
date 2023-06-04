package woowacourse.shopping.ui.order.history

import woowacourse.shopping.data.mapper.toUiModel
import woowacourse.shopping.domain.repository.OrderProductRepository
import woowacourse.shopping.ui.order.history.OrderHistoryContract.Presenter
import woowacourse.shopping.ui.order.history.OrderHistoryContract.View

class OrderHistoryPresenter(
    view: View,
    private val orderProductRepository: OrderProductRepository,
) : Presenter(view) {

    override fun loadOrderedProducts() {
        orderProductRepository.requestOrders(
            onSuccess = { products ->
                view.showOrderedProducts(products.map { it.toUiModel() })
            },
            onFailure = { },
        )
    }

    override fun navigateToHome(itemId: Int) {
        when (itemId) {
            android.R.id.home -> {
                view.navigateToHome()
            }
        }
    }
}
