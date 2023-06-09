package woowacourse.shopping.ui.orderdetail

import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.OrderProduct
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.utils.ActivityUtils.showErrorMessage

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {
    override fun gerOrderProducts(id: Int) {
        orderRepository.getOrderHistory(
            id,
            { history ->
                view.setOrderHistory(history.toUIModel())
                view.setOrderList(history.orderItems.map(OrderProduct::toUIModel))
            },
            { showErrorMessage(it.message) },
        )
    }
}
