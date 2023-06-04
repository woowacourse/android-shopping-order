package woowacourse.shopping.feature.orderDetail

import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toPresentation

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val id: Int,
    private val orderRepository: OrderRepository
) : OrderDetailContract.Presenter {

    override fun loadProducts() {
        orderRepository.getOrderDetail(
            id,
            callback = {
                it.onSuccess { orderInfo ->
                    view.initAdapter(orderInfo.products.map { it.toPresentation() })
                    view.setUpView(orderInfo.toPresentation())
                }.onFailure { throwable ->
                    view.showErrorMessage(throwable)
                }
            }
        )
    }

    override fun cancelOrder(id: Int) {
        orderRepository.cancelOrder(
            id,
            callback = {
                it.onSuccess {
                }.onFailure { throwable ->
                    view.showErrorMessage(throwable)
                }
            }
        )
    }
}
