package woowacourse.shopping.feature.order.detail

import com.example.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toPresentation

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {
    override fun requestOrderDetail(orderId: Long) {
        orderRepository.getOrderDetail(
            orderId,
            onSuccess = { orderDetail ->
                view.showOrderDetail(orderDetail.toPresentation())
            },
            onFailure = {},
        )
    }
}
