package woowacourse.shopping.feature.payment

import com.example.domain.model.OrderProduct
import com.example.domain.model.Point
import com.example.domain.repository.CartRepository
import com.example.domain.repository.OrderRepository
import com.example.domain.repository.PointRepository
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel

class PaymentPresenter(
    override val view: PaymentContract.View,
    private val cartRepository: CartRepository,
    private val pointRepository: PointRepository,
    private val orderRepository: OrderRepository
) : PaymentContract.Presenter {

    private lateinit var orderProducts: List<OrderProduct>
    private lateinit var point: Point

    override fun loadCartProducts(cartProductIds: List<Int>) {

        cartRepository.getAll(
            onSuccess = {
                val cartProducts = mutableListOf<CartProductUiModel>()
                val orders = mutableListOf<OrderProduct>()
                cartProductIds.forEach { id ->
                    it.findById(id)?.let { cartProduct ->
                        cartProducts.add(cartProduct.toPresentation())
                        orders.add(OrderProduct(cartProduct.count, cartProduct.product))
                    }
                }
                orderProducts = orders
                view.showCartProducts(cartProducts.toList())
            },
            onFailure = { view.showFailureMessage(it.message) }
        )
    }

    override fun loadPoint() {
        pointRepository.getPoint(
            onSuccess = {
                view.showPoint(it.toPresentation())
                point = it
            },
            onFailure = { view.showFailureMessage(it.message) }
        )
    }

    override fun placeOrder(usedPoint: Int) {
        orderRepository.placeOrder(
            usedPoint,
            orderProducts,
            onSuccess = {
                view.showPaymentDoneScreen()
            },
            onFailure = { view.showFailureMessage(it.message) }
        )
    }

    override fun useAllPoint() {
        view.setPoint(point.currentPoint)
    }
}
