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

            view.setOrderProductItemView(orderProducts.getAll().map { it.toUIModel() })
            view.setOrderDateView(orderDetail.orderDateTime)
            view.setOrderPriceView(orderProducts.totalPrice)
            view.setUsedPointView(orderDetail.usedPoint)
            view.setSavedPointView(orderDetail.savedPoint)
            view.setTotalPriceView(orderProducts.totalPrice - orderDetail.usedPoint)
        }
    }

    private fun onFailure() {
        view.handleErrorView()
    }
}
