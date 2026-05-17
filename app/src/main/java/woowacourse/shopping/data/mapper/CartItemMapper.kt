package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.dto.CartItemDto
import woowacourse.shopping.data.remote.dto.CartResponseDto
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.cart.CartItems
import woowacourse.shopping.domain.model.cart.Quantity

fun CartResponseDto.toDomain(): CartItems =
    CartItems(
        values = content.toDomain(),
        isLast = last,
        isFirst = first,
        totalPages = totalPages,
    )

fun List<CartItemDto>.toDomain(): List<CartItem> = map { it.toDomain() }

fun CartItemDto.toDomain(): CartItem =
    CartItem(
        id = id,
        product = product.toDomain(),
        quantity = Quantity(quantity),
    )
