package woowacourse.shopping.feature.orderDetail

import com.example.domain.repository.OrderRepository
import woowacourse.shopping.model.toPresentation

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository
) : OrderDetailContract.Presenter {

    private var orderId = -1

    override fun loadOrderDetail(orderId: Int) {
        this.orderId = orderId
        orderRepository.getOrderDetail(
            orderId = orderId,
            onSuccess = {
                view.showOrderDetail(orderDetail = it.toPresentation())
            },
            onFailure = {}
        )
    }

    override fun cancelOrder() {
        orderRepository.cancelOrder(
            orderId = orderId,
            onSuccess = {
                view.moveToMainScreen()
            },
            onFailure = {}
        )
    }
}
