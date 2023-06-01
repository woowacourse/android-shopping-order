package woowacourse.shopping.ui.order

import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.PointRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toUi
import woowacourse.shopping.model.UiOrder
import woowacourse.shopping.model.UiPoint
import woowacourse.shopping.ui.order.OrderContract.View
import woowacourse.shopping.ui.order.recyclerview.ListItem

class OrderPresenter(
    private val view: View,
    order: UiOrder,
    private val orderRepository: OrderRepository,
    private val pointRepository: PointRepository,
) : OrderContract.Presenter {
    private val originOrder: Order = order.toDomain()
    private var discountedOrder: Order = originOrder.copy()
    private var maxAvailablePoint: Point = DEFAULT_MAX_POINT

    override fun fetchAll() {
        pointRepository.getPoint(
            onSuccess = { fetchedPoint ->
                maxAvailablePoint = fetchedPoint
                updateOrder(discountedOrder.orderProducts.toUi() + maxAvailablePoint.toUi())
                view.showFinalPayment(discountedOrder.finalPrice.toUi())
            },
            onFailed = {
                maxAvailablePoint = DEFAULT_POINT
                updateOrder(discountedOrder.orderProducts.toUi() + maxAvailablePoint.toUi())
                view.showFinalPayment(discountedOrder.finalPrice.toUi())
                view.showOrderLoadFailed()
            }
        )
    }

    private fun updateOrder(orderItems: List<ListItem>) {
        view.updateOrder(orderItems)
    }

    override fun applyPoint(point: UiPoint) {
        if (point.toDomain() <= maxAvailablePoint && point.value != 0) {
            discountedOrder = originOrder.applyPointDiscount(point.toDomain())
            view.showFinalPayment(discountedOrder.finalPrice.toUi())
        }
    }

    override fun order() {
        orderRepository.order(
            order = discountedOrder,
            onSuccess = {
                view.showOrderCompleted()
                view.navigateToHome()
            },
            onFailed = { view.showOrderFailed() }
        )
    }

    override fun navigateToHome() {
        view.navigateToHome()
    }

    companion object {
        private val DEFAULT_MAX_POINT = Point(0)
        private val DEFAULT_POINT = Point(0)
    }
}
