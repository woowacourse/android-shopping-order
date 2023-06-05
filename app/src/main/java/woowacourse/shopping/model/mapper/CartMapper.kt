package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.model.CartModel

fun CartModel.toDomain(): Cart = Cart(
    items = cartProducts.map { it.toDomain() },
)

fun Cart.toUi(): CartModel = CartModel(
    cartProducts = items.map { it.toUi() },
)
