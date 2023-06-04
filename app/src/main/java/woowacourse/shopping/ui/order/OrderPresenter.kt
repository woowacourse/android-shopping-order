package woowacourse.shopping.ui.order

import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.MemberRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.model.mapper.CartProductMapper.toView

class OrderPresenter(
    private val view: OrderContract.View,
    private val cartRepository: CartRepository,
    private val memberRepository: MemberRepository,
    private val orderRepository: OrderRepository
) : OrderContract.Presenter {
    private lateinit var cart: Cart
    private var points: Int = 0
    private var usePoints: Int = 0

    override fun loadProducts(ids: List<Int>) {
        cartRepository.getAll(
            onSuccess = {products ->
                cart = Cart(products.filter { ids.contains(it.id) })
                view.showProducts(cart.cartProducts.map { it.toView() })
                view.showOriginalPrice(cart.totalPrice)
                view.updateFinalPrice(cart.totalPrice)
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
        if(::cart.isInitialized) {
            usePoints(points)
        }
    }

    override fun usePoints(use: Int) {
        usePoints = if(use > points) {
            view.notifyPointsExceeded()
            points
        }
        else {
            use
        }

        view.updatePointsUsed(usePoints)
        discountPrice(usePoints)
    }

    private fun discountPrice(discountPrice: Int) {
        view.updateDiscountPrice(discountPrice)
        view.updateFinalPrice(cart.totalPrice - discountPrice)
    }

    override fun order() {
        if(::cart.isInitialized) {
            orderRepository.order(
                cart,
                usePoints,
                onSuccess = {
                    view.showOrderDetail(it)
                },
                onFailure = {
                    view.notifyOrderFailed()
                }
            )
        }
    }
}