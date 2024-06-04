package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.remote.CartDto
import woowacourse.shopping.domain.model.Cart

fun CartDto.toDomain(): Cart {
    return Cart(
        id = this.id,
        quantity = this.quantity,
        product = this.product.toDomain(),
    )
}
