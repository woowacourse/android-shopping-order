package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.remote.CartItemIdDto
import woowacourse.shopping.domain.model.CartItemId

fun CartItemIdDto.toDomain(): CartItemId {
    return CartItemId(id = this.id)
}
