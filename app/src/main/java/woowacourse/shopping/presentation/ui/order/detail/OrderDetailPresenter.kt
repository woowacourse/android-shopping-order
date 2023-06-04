package woowacourse.shopping.presentation.ui.order.detail

import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.util.WoowaResult

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {
    override fun fetchOrderDetail(orderId: Long) {
        orderRepository.fetchOrder(orderId) { result ->
            when (result) {
                is WoowaResult.SUCCESS -> {
                    view.showOrderDateTime(result.data.orderedDateTime)
                    view.showOrderDetail(result.data.products)
                }
                is WoowaResult.FAIL -> view.showUnexpectedError()
            }
        }
    }
}
