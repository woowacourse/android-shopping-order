package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.mapper.toUiModel
import woowacourse.shopping.ui.model.OrderUiModel

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
    private val orderId: Int,
    private val order: OrderUiModel?,
) : OrderDetailContract.Presenter {

    override fun getOrder() {
        if (orderId != -1) {
            orderRepository.getOrder(orderId)
                .thenAccept {
                    val order = it.getOrThrow().toUiModel()

                    view.initView(order)
                }
                .exceptionally { error ->
                    error.message?.let { view.showErrorMessage(it) }
                    null
                }
        }

        order?.let {
            view.initView(it)
        }
    }

    override fun handleNavigator() {
        order?.let {
            return view.navigator.navigateToPreviousView(
                onFailed = view::showErrorMessage
            )
        }

        view.navigator.navigateToShoppingView()
    }
}
