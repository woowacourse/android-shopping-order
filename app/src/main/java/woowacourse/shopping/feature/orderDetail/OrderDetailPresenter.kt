package woowacourse.shopping.feature.orderDetail

import com.example.domain.repository.OrderRepository
import woowacourse.shopping.model.toPresentation

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository
) : OrderDetailContract.Presenter {
    override fun loadOrderDetail(orderId: Int) {
        val orderDetail = orderRepository.getOrderDetail(orderId)
        view.showOrderDetail(orderDetail = orderDetail.toPresentation())
    }
}
