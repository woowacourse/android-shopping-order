package woowacourse.shopping.domain

import woowacourse.shopping.domain.entity.CartProduct

fun fakeCartProduct(
    productId: Long = 1L,
    price: Int = 1000,
    count: Int = 1,
) = CartProduct(
    fakeProduct(productId, price),
    count,
)