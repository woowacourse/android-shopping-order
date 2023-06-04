package woowacourse.shopping.feature.order

import com.example.domain.model.OrderDetailProduct
import com.example.domain.model.Point
import com.example.domain.repository.CartRepository
import com.example.domain.repository.OrderRepository
import com.example.domain.repository.PointRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toPresentation

class OrderPresenter(
    private val view: OrderContract.View,
    private val cartRepository: CartRepository,
    private val pointRepository: PointRepository,
    private val orderRepository: OrderRepository,
) : OrderContract.Presenter {

    private val orderProducts by lazy {
        val cartProducts = cartRepository.getAll()
        cartProducts.filter { it.isSelected }.map { it.toPresentation() }
    }

    override fun loadProducts() {
        view.initAdapter(orderProducts)
    }

    override fun loadPayment() {
        val sumOfProductPrice = orderProducts.sumOf { it.totalPrice() }
        pointRepository.getPoint(
            onSuccess = { view.setUpView(it.currentPoint, sumOfProductPrice) },
            onFailure = {
                view.showErrorMessage(Throwable("오류 발생"))
            }
        )
    }

    override fun orderProducts(usedPoint: Int) {
        val orders =
            orderProducts.map { OrderDetailProduct(it.count, it.productUiModel.toDomain()) }
        orderRepository.addOrder(
            Point(usedPoint), orders,
            callback = {
                it.onSuccess {
                }.onFailure { throwable ->
                    view.showErrorMessage(throwable)
                }
            }
        )
    }
}
