package woowacourse.shopping.data.remote.dto.mapper

import woowacourse.shopping.data.remote.dto.response.CartResponse
import woowacourse.shopping.domain.CartProduct

fun CartResponse.toDomain(): CartProduct {
    return CartProduct(
        cartId = id.toLong(),
        productId = product.id.toLong(),
        name = product.name,
        price = product.price.toLong(),
        imgUrl = product.imageUrl,
        category = product.category,
        quantity = quantity,
    )
}
