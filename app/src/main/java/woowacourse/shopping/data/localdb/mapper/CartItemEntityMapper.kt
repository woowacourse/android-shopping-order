package woowacourse.shopping.data.localdb.mapper

import woowacourse.shopping.data.localdb.entity.CartItemQuantityEntity
import woowacourse.shopping.model.CartItem

fun CartItem.toEntity(): CartItemQuantityEntity =
    CartItemQuantityEntity(
        productId = product.id,
        cartItemId = id,
        quantity = quantity,
    )
