package woowacourse.shopping.model.mapper

import woowacourse.shopping.domain.model.DomainCart
import woowacourse.shopping.model.CartModel

fun CartModel.toDomain(): DomainCart = DomainCart(
    items = cartProducts.map { it.toDomain() },
)

fun DomainCart.toUi(): CartModel = CartModel(
    cartProducts = items.map { it.toUi() },
)
