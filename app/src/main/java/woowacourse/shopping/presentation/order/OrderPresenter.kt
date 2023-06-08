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

    private lateinit var orderingCartProducts: CartProducts

    override fun loadOrderCarts(orderCartIds: List<Long>) {
        cartRepository.getCartProducts { totalCarts ->
            convertOrderingCarts(totalCarts, orderCartIds)
            showOrderInfo()
        }
    }

    private fun convertOrderingCarts(
        totalCarts: List<CartProduct>,
        orderCartIds: List<Long>,
    ) {
        val orderingCarts = totalCarts.filter { it.cartId in orderCartIds }
        orderingCartProducts = CartProducts(orderingCarts)
    }

    private fun showOrderInfo() {
        view.showOrderCartProducts(orderingCartProducts.items.toPresentation())
        view.showTotalPrice(orderingCartProducts.getSelectedProductsPrice())
    }

    override fun orderCartProducts() {
        orderRepository.orderCartProducts(orderingCartProducts.items) { orderId ->
            view.showOrderDetail(orderId)
        }
    }

    private fun List<CartProduct>.toPresentation(): List<CartProductModel> {
        return this.map { it.toPresentation() }
    }

    override fun loadCash() {
        cashRepository.loadCash { cash ->
            view.showCash(cash)
        }
    }

    override fun chargeCash(cash: Int) {
        cashRepository.chargeCash(cash) {
            view.showCash(it)
        }
    }
}
