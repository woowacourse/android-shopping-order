package woowacouse.shopping.model.order

import woowacouse.shopping.model.card.Card
import woowacouse.shopping.model.cart.CartProducts
import woowacouse.shopping.model.point.Point

data class Order(
    val id: Long,
    val cartProducts: CartProducts,
    val usePoint: Point,
    val card: Card,
) {
    fun getOrderPrice(): Int = cartProducts.totalPrice
    fun getTotalPrice(): Int = cartProducts.totalPrice - usePoint.getPoint()
    fun getCardNumber(): String = card.number
}
