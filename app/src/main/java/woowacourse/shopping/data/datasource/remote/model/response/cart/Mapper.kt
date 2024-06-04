package woowacourse.shopping.data.datasource.remote.model.response.cart

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartPageAttribute
import woowacourse.shopping.domain.model.Quantity

fun CartContent.toCartItem() = CartItem(id, product.id, Quantity(quantity))

fun CartResponse.toCartItems() = cartContent.map { it.toCartItem() }

fun CartResponse.toCartPage() = CartPageAttribute(totalPages, totalElements, first, last)
