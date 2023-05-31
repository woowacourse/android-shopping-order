package woowacourse.shopping.ui.order

import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.PointRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toUi
import woowacourse.shopping.model.UiOrder
import woowacourse.shopping.ui.order.OrderContract.View
import woowacourse.shopping.ui.order.recyclerview.ListItem

class OrderPresenter(
    private val view: View,
    order: UiOrder,
    private val orderRepository: OrderRepository,
    private val pointRepository: PointRepository,
) : OrderContract.Presenter {
    private var order: Order = order.toDomain()
    private var availablePoint: Point = Point(0)

    override fun fetchAll() {
        pointRepository.getPoint(
            onSuccess = { fetchedPoint ->
                availablePoint = fetchedPoint
                updateOrder(order.orderProducts.toUi() + availablePoint.toUi())
                view.showTotalPayment(order.totalPayment.toUi())
            },
            onFailed = {
                availablePoint = Point(2000)
                updateOrder(order.orderProducts.toUi() + availablePoint.toUi())
                view.showTotalPayment(order.totalPayment.toUi())
                view.showOrderLoadFailed()
            }
        )
    }

    private fun updateOrder(orderItems: List<ListItem>) {
        view.updateOrder(orderItems)
    }

    override fun order() {
        orderRepository.order(
            order = order,
            onSuccess = { view.showOrderCompleted() },
            onFailed = { view.showOrderFailed() }
        )
    }


}
