package woowacourse.shopping.domain

import woowacourse.shopping.domain.entity.CartProduct

fun fakeCartProduct(
    productId: Long = 1L,
    count: Int = 1,
) = CartProduct(
    product(productId),
    count,
)
