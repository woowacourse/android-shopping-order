package woowacourse.shopping.domain

data class Cart(
    val cartId: Long,
    val product: Product,
    val quantity: Int,
)
