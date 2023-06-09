package woowacourse.shopping.presentation.ui.order.detail

import woowacourse.shopping.domain.repository.OrderRepository

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {
    override fun fetchOrderDetail(orderId: Long) {
        orderRepository.fetchOrder(orderId) { result ->
            result
                .onSuccess {
                    view.showOrderDateTime(it.orderedDateTime)
                    view.showOrderDetail(it.products)
                }
                .onFailure { view.showError(it.message ?: "에러 메시지가 없습니다.") }
        }
    }
}
