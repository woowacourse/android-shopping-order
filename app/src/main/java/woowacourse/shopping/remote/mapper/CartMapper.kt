package woowacourse.shopping.remote.mapper

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.remote.model.response.CartResponse

fun CartResponse.toDomain(): Cart {
    return Cart(
        id = this.id,
        quantity = this.quantity,
        product = this.productResponse.toDomain(),
    )
}
