package woowacourse.shopping.ui.order

import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.MemberRepository
import woowacourse.shopping.ui.model.mapper.CartProductMapper.toView

class OrderPresenter(
    private val view: OrderContract.View,
    private val cartRepository: CartRepository,
    private val memberRepository: MemberRepository
) : OrderContract.Presenter {
    private lateinit var cart: Cart
    private var points: Int = 0

    override fun loadProducts(ids: List<Int>) {
        cartRepository.getAll(
            onSuccess = {products ->
                cart = Cart(products.filter { ids.contains(it.id) })
                view.showProducts(cart.cartProducts.map { it.toView() })
                view.showOriginalPrice(cart.totalPrice)
            },
            onFailure = {}
        )
    }

    override fun loadPoints() {
        memberRepository.getPoints(
            onSuccess = {
                points = it
                view.showPoints(it)
            },
            onFailure = {}
        )
    }

    override fun useAllPoints() {
        usePoints(points)
    }

    override fun usePoints(points: Int) {
        view.updatePointsUsed(points)
        discountPrice(points)
    }

    private fun discountPrice(discountPrice: Int) {
        view.updateDiscountPrice(discountPrice)
        view.updateFinalPrice(cart.totalPrice - discountPrice)
    }
}