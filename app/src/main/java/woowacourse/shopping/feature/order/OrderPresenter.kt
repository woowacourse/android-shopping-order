package woowacourse.shopping.feature.order

import com.example.domain.repository.CartRepository
import com.example.domain.repository.PointRepository
import woowacourse.shopping.mapper.toPresentation

class OrderPresenter(
    private val view: OrderContract.View,
    private val cartRepository: CartRepository,
    private val pointRepository: PointRepository,
) : OrderContract.Presenter {

    private val orderProducts by lazy {
        val cartProducts = cartRepository.getAll()
        cartProducts.filter { it.isSelected }.map { it.toPresentation() }
    }

    override fun loadProducts() {
//        val cartProducts = cartRepository.getAll()
//        val orderProducts = cartProducts.filter { it.isSelected }.map { it.toPresentation() }
        view.initAdapter(orderProducts)
    }

    override fun loadPayment() {
//        val cartProducts = cartRepository.getAll()
//        val orderProducts = cartProducts.filter { it.isSelected }.map { it.toPresentation() }
        val sumOfProductPrice = orderProducts.sumOf { it.totalPrice() }
        pointRepository.getPoint(
            onSuccess = { view.setUpView(it, sumOfProductPrice) },
            onFailure = {}
        )
    }

    override fun orderProducts(usedPoint: Int) {
        // orderRepository.order(totalPrice)
    }
}
