package woowacourse.shopping.data.remote.dto.mapper

import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.domain.CartProduct

fun ProductResponse.toDomain(): CartProduct {
    return CartProduct(
        productId = id.toLong(),
        name = name,
        price = price.toLong(),
        imgUrl = imageUrl,
        category = category,
        quantity = 0,
    )
}
