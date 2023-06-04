package woowacourse.shopping.feature.order.detail

import com.example.domain.repository.OrderRepository
import woowacourse.shopping.model.mapper.toUi

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderId: Long,
    private val orderRepository: OrderRepository
) : OrderDetailContract.Presenter {

    override fun loadOrderInformation() {
        orderRepository.requestFetchOrderById(
            id = orderId,
            onSuccess = { order ->
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
            onFailure = {
                view.showAccessError()
                view.closeOrderDetail()
            }
        )
    }
}
