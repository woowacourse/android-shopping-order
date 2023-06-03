package woowacourse.shopping.feature.orderDetail

import com.example.domain.repository.OrderRepository
import woowacourse.shopping.model.toPresentation

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository
) : OrderDetailContract.Presenter {
    override fun loadOrderDetail(orderId: Int) {
        orderRepository.getOrderDetail(
            orderId = orderId,
            onSuccess = {
                view.showOrderDetail(orderDetail = it.toPresentation())
            },
            onFailure = {}
        )
    }
}
