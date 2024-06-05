package woowacourse.shopping.domain

import woowacourse.shopping.domain.entity.CartProduct

fun cartProduct(
    productId: Long = 1L,
    count: Int = 1,
) = CartProduct(
    product(productId),
    count,
)
