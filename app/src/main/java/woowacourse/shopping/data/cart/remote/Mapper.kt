package woowacourse.shopping.data.cart.remote

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartPageAttribute
import woowacourse.shopping.domain.model.Quantity

fun Content.toCartItem() = CartItem(id, product.id, Quantity(quantity))

fun CartResponse.toCartItems() = content.map { it.toCartItem() }

fun CartResponse.toCartPage() = CartPageAttribute(totalPages, totalElements, first, last)
