package woowacourse.shopping.presentation.view.orderdetail

import woowacourse.shopping.presentation.mapper.toUIModel
import woowacouse.shopping.data.repository.order.OrderRepository
import woowacouse.shopping.model.cart.CartProducts

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {
    override fun loadOrderDetail(orderId: Long) {
        orderRepository.loadOrder(orderId, ::onFailure) { orderDetail ->
            val orderProducts = CartProducts(orderDetail.products)

            view.showOrderProductItemView(orderProducts.getAll().map { it.toUIModel() })
            view.showOrderDateView(orderDetail.orderDateTime)
            view.showOrderPriceView(orderProducts.totalPrice)
            view.showUsedPointView(orderDetail.usedPoint)
            view.showSavedPointView(orderDetail.savedPoint)
            view.showTotalPriceView(orderProducts.totalPrice - orderDetail.usedPoint)
        }
    }

    private fun onFailure(message: String) {
        view.handleErrorView(message)
    }
}
