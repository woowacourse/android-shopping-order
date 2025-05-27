package woowacourse.shopping.data.util.mapper

import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.domain.model.CartItem


fun CartResponse.toCartItems():List<CartItem> =
    this.content.map{CartItem(it.product.toDomain(),it.quantity)}