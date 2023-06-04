package woowacourse.shopping.presentation.view.orderdetail

import com.example.domain.cart.CartProducts
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.mapper.toUIModel
import woowacourse.shopping.data.respository.order.OrderRepository

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderId: Long,
    private val orderRepository: OrderRepository,
) : OrderDetailContract.Presenter {

    private lateinit var orderProductsDomain: CartProducts

    override fun initView() {
        orderRepository.requestOrder(orderId, ::onFailure) { orderDetailEntity ->
            val orderProductsUi = orderDetailEntity.products.map { it.toUIModel() }
            view.setView(orderDetailEntity, orderProductsUi)

            orderProductsDomain = CartProducts(orderProductsUi.map { it.toDomain() })
            val totalPrice = orderProductsDomain.totalCheckedPrice - orderDetailEntity.usedPoint
            view.setTotalPriceView(totalPrice)
        }
    }

    private fun onFailure() {
        view.handleErrorView()
    }
}
