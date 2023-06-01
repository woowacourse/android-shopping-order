package woowacouse.shopping.model.order

import woowacouse.shopping.model.card.Card
import woowacouse.shopping.model.cart.CartProducts
import woowacouse.shopping.model.point.Point

data class Order(
    val id: Long,
    val cartProducts: CartProducts,
    val point: Point,
    val card: Card,
)
