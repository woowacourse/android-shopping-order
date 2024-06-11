package woowacourse.shopping.domain.entity

fun fakeCartProduct(
    productId: Long = 1L,
    price: Int = 1000,
    count: Int = 1,
) = CartProduct(
    fakeProduct(productId, price),
    count,
)
