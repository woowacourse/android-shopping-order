package woowacourse.shopping.data.cart.remote

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Quantity

fun Content.toCartItem() = CartItem(id, product, Quantity(quantity))

fun CartResponse.toCartItems() = content.map { it.toCartItem() }
