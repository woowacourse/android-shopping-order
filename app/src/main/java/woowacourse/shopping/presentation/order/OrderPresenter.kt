package woowacourse.shopping.presentation.order

import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.cash.CashRepository
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.presentation.mapper.toPresentation
import woowacourse.shopping.presentation.model.CartProductModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val cartRepository: CartRepository,
    private val cashRepository: CashRepository,
) : OrderContract.Presenter {

    override fun loadOrderCarts(orderCartIds: List<Long>) {
        cartRepository.getCartProducts { cartProducts ->
            val cartProductModels =
                cartProducts.filter { it.cartId in orderCartIds }.toPresentation()
            view.showOrderCartProducts(cartProductModels)
        }
    }

    private fun List<CartProduct>.toPresentation(): List<CartProductModel> {
        return this.map { it.toPresentation() }
    }

    override fun loadCash() {
        cashRepository.getCash { view.showCash(it) }
    }

    override fun chargeCash(cash: Int) {
        cashRepository.chargeCash(cash) {
            view.showCash(it)
        }
    }
}
