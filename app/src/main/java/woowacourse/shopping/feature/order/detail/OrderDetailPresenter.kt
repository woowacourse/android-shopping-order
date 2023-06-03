package woowacourse.shopping.feature.order.detail

import com.example.domain.repository.OrderRepository

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    orderId: Long,
    orderRepository: OrderRepository
) : OrderDetailContract.Presenter {

    private val orderInfo = orderRepository.getOrderInfo(orderId)
}
