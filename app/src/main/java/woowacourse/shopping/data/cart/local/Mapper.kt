package woowacourse.shopping.data.cart.local

import woowacourse.shopping.data.cart.local.entity.CartItemEntity
import woowacourse.shopping.domain.model.CartItem

fun CartItemEntity.toCartItem() = CartItem(id, productId, quantity)

fun List<CartItemEntity>.toCartItems() = map { it.toCartItem() }

fun CartItem.toCartItemEntity() = CartItemEntity(id, productId, quantity)

fun List<CartItem>.toCartItemEntities() = map { it.toCartItemEntity() }
