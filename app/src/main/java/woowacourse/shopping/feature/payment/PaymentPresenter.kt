package woowacourse.shopping.feature.payment

import com.example.domain.repository.CartRepository
import com.example.domain.repository.PointRepository
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel

class PaymentPresenter(
    override val view: PaymentContract.View,
    private val cartRepository: CartRepository,
    private val pointRepository: PointRepository
) : PaymentContract.Presenter {
    override fun loadCartProducts(cartProductIds: List<Int>) {
        val cartProducts = mutableListOf<CartProductUiModel>()
        val all = cartRepository.getAll()
        cartProductIds.forEach { id ->
            all.findById(id)?.let {
                cartProducts.add(it.toPresentation())
            }
        }
        view.showCartProducts(cartProducts.toList())
    }

    override fun loadPoint() {
        val point = pointRepository.getPoint()
        view.showPoint(point.toPresentation())
    }
}
