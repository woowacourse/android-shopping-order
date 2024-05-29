package woowacourse.shopping.data.local.mapper

import woowacourse.shopping.data.local.entity.RecentProductEntity
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct

fun RecentProductEntity.toDomain(): RecentProduct {
    return RecentProduct(
        productId,
        name,
        imgUrl,
        quantity,
        price,
        createdAt,
        category, cartId
    )
}

fun RecentProduct.toEntity(): RecentProductEntity {
    return RecentProductEntity(
        productId, name, imgUrl, quantity, price, createdAt, category, cartId
    )
}

fun RecentProduct.toCartProduct(): CartProduct {
    return CartProduct(
        productId, name, imgUrl, price, quantity, category, cartId
    )
}