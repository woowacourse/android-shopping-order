package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.repository.OrderRepository

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {
    override fun gerOrderProducts(id: Int) {
        orderRepository.getOrderHistory(id) { history ->
            history?.let {
                view.setOrderHistory(it.toUIModel())
                view.setOrderList(it.orderItems.map { orderItem -> orderItem.toUIModel() })
            } ?: throw IllegalArgumentException("주문 내역을 가져오는데 실패했습니다.")

/*            view.setOrderHistory(history?.toUIModel() ?: throw IllegalArgumentException("주문 내역을 가져오는데 실패했습니다."))
            view.setOrderList(
                history?.orderItems?.map { orderItem -> orderItem.toUIModel() }
                    ?: throw IllegalArgumentException("주문 내역을 가져오는데 실패했습니다."),
            )*/
        }
    }
}
