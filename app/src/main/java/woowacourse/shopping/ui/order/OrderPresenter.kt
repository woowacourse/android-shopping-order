package woowacourse.shopping.ui.order

import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.discount.Discountable
import woowacourse.shopping.domain.model.discount.Point
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.PointRepository
import woowacourse.shopping.model.OrderModel
import woowacourse.shopping.model.PointModel
import woowacourse.shopping.model.mapper.toDomain
import woowacourse.shopping.model.mapper.toPointModelUi
import woowacourse.shopping.model.mapper.toUi
import woowacourse.shopping.ui.order.OrderContract.View
import woowacourse.shopping.ui.order.recyclerview.ListItem

class OrderPresenter(
    private val view: View,
    order: OrderModel,
    private val orderRepository: OrderRepository,
    private val pointRepository: PointRepository,
    private var maxAvailablePoint: Discountable = DEFAULT_MAX_POINT
) : OrderContract.Presenter {
    private val originOrder: Order = order.toDomain()
    private var discountedOrder: Order = originOrder.copy()

    override fun fetchAll() {
        pointRepository.getPoint(
            onSuccess = ::updateAvailablePoint,
            onFailed = {
                updateAvailablePoint(DEFAULT_POINT)
                view.showOrderLoadFailed()
            }
        )
    }

    private fun updateAvailablePoint(point: Discountable) {
        maxAvailablePoint = point
        updateOrder(discountedOrder.orderProducts.toUi() + maxAvailablePoint.toPointModelUi())
        view.showFinalPayment(discountedOrder.finalPrice.toUi())
    }

    private fun updateOrder(orderItems: List<ListItem>) {
        view.showOrders(orderItems)
    }

    override fun applyPoint(pointModel: PointModel) {
        val point = pointModel.toDomain()
        if (maxAvailablePoint.discountable(point)) {
            discountedOrder = originOrder.discount(point)
            view.showFinalPayment(discountedOrder.finalPrice.toUi())
        }
    }

    override fun order() {
        orderRepository.saveOrder(
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
