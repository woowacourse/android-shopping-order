package woowacourse.shopping.presentation.order

import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.cash.CashRepository
import woowacourse.shopping.data.order.OrderRepository
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.CartProducts
import woowacourse.shopping.presentation.mapper.toPresentation
import woowacourse.shopping.presentation.model.CartProductModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val cartRepository: CartRepository,
    private val cashRepository: CashRepository,
    private val orderRepository: OrderRepository,
) : OrderContract.Presenter {

    private lateinit var totalCartProducts: CartProducts

    override fun loadOrderCarts(orderCartIds: List<Long>) {
        cartRepository.getCartProducts { cartProducts ->
            totalCartProducts = CartProducts(cartProducts)
            val cartProductModels =
                cartProducts.filter { it.cartId in orderCartIds }.toPresentation()
            view.showOrderCartProducts(cartProductModels)
            view.showTotalPrice(CartProducts(cartProducts).getSelectedProductsPrice())
        }
    }

    override fun orderCartProducts() {
        orderRepository.orderCartProducts(totalCartProducts.items) { orderId ->
            view.showOrderDetail(orderId)
        }
    }

    private fun List<CartProduct>.toPresentation(): List<CartProductModel> {
        return this.map { it.toPresentation() }
    }

    override fun loadCash() {
        cashRepository.loadCash { view.showCash(it) }
    }

    override fun chargeCash(cash: Int) {
        cashRepository.chargeCash(cash) {
            view.showCash(it)
        }
    }
}
