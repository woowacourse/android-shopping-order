package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.mapper.toUiModel
import woowacourse.shopping.ui.orderdetail.OrderDetailNavigation.PreviousViewCase
import woowacourse.shopping.ui.orderdetail.OrderDetailNavigation.ShoppingViewCase

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
    private val navigation: OrderDetailNavigation,
) : OrderDetailContract.Presenter {

    override fun getOrder() {
        when (navigation) {
            is PreviousViewCase -> view.initView(navigation.order)
            is ShoppingViewCase -> orderRepository.getOrder(navigation.orderId)
                .thenAccept {
                    val order = it.getOrThrow().toUiModel()

                    view.initView(order)
                }
                .exceptionally { error ->
                    error.message?.let { view.showErrorMessage(it) }
                    null
                }
        }
    }

    override fun handleNavigator() {
        navigation.navigate()
    }
}
