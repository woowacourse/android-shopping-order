package woowacourse.shopping.feature.payment

import android.os.Handler
import android.os.Looper
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

    private val handler = Handler(Looper.getMainLooper())

    override fun loadCartProducts(cartProductIds: List<Int>) {
        Thread {
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
            handler.post { view.showCartProducts(cartProducts.toList()) }
        }.start()
    }

    override fun loadPoint() {
        pointRepository.getPoint(
            onSuccess = {
                point = it
                view.showPoint(it.toPresentation())
            },
            onFailure = {}
        )

    }

    override fun placeOrder(usedPoint: Int) {
        orderRepository.placeOrder(
            usedPoint,
            orderProducts,
            onSuccess = {
                view.showPaymentDoneScreen()
            },
            onFailure = {}
        )
    }

    override fun useAllPoint() {
        view.setPoint(point.currentPoint)
    }
}
