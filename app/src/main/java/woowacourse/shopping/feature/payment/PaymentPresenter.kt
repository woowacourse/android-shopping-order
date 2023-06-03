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
        val cartProducts = mutableListOf<CartProductUiModel>()
        val orders = mutableListOf<OrderProduct>()
        val all = cartRepository.getAll()
        cartProductIds.forEach { id ->
            all.findById(id)?.let {
                cartProducts.add(it.toPresentation())
                orders.add(OrderProduct(it.count, it.product))
            }
        }
        orderProducts = orders
        view.showCartProducts(cartProducts.toList())
    }

    override fun loadPoint() {
        point = pointRepository.getPoint()
        view.showPoint(point.toPresentation())
    }

    override fun placeOrder(usedPoint: Int) {
        orderRepository.placeOrder(
            usedPoint,
            orderProducts,
            onSuccess = {},
            onFailure = {}
        )
        view.showPaymentDoneScreen()
    }

    override fun useAllPoint() {
        view.setPoint(point.currentPoint)
    }
}
