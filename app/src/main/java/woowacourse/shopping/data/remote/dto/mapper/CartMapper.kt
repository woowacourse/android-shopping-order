package woowacourse.shopping.data.remote.dto.mapper

import woowacourse.shopping.data.remote.dto.response.Cart
import woowacourse.shopping.domain.CartProduct


fun Cart.toDomain(): CartProduct {
    return CartProduct(
        productId = id.toLong(),
        name = product.name,
        price = product.price.toLong(),
        imgUrl = product.imageUrl,
        category = product.category,
        quantity = quantity
    )
}