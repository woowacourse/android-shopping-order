package woowacourse.shopping.feature.order.detail

import woowacourse.shopping.data.order.OrderRepository
import woowacourse.shopping.model.mapper.toUi

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderId: Long,
    private val orderRepository: OrderRepository
) : OrderDetailContract.Presenter {

    override fun loadOrderInformation() {
        orderRepository.requestFetchOrderById(
            id = orderId,
            success = { order ->
                order!!
                view.setViewFixedContents(order.toUi())
                view.setProductsSummary(
                    firstProductName = order.orderProducts[0].product.name,
                    productsCount = order.orderProducts.count(), originalPrice = order.originalPrice
                )
                view.setOrderDate(order.orderDate)
                view.setOrderNumber(orderId)
                view.setOrderProducts(order.orderProducts)
            },
            failure = {
                view.showAccessError()
                view.closeOrderDetail()
            }
        )
    }
}
