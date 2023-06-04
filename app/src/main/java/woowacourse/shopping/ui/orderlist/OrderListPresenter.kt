package woowacourse.shopping.ui.orderlist

import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.repository.OrderRepository

class OrderListPresenter(
    private val view: OrderListContract.View,
    private val orderRepository: OrderRepository,
) : OrderListContract.Presenter {
    override fun getOrderList() {
        orderRepository.getOrderList {
            view.setOrderList(
                it?.map { history -> history.toUIModel() }
                    ?: throw IllegalArgumentException("주문 내역을 가져오는데 실패했습니다."),
            )
        }
    }

    override fun showOrderDetail(orderId: Int) {
        view.navigateToOrderDetail(orderId)
    }
}
