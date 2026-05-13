package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.dto.CartItemDto
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.cart.CartItems
import woowacourse.shopping.domain.cart.Quantity

fun List<CartItemDto>.toDomain(): CartItems =
    CartItems(
        values = map { it.toDomain() },
    )

fun CartItemDto.toDomain(): CartItem =
    CartItem(
        product = product.toDomain(),
        quantity = Quantity(quantity),
    )
