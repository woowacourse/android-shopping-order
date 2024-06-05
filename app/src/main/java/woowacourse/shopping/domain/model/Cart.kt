package woowacourse.shopping.domain.model

data class Cart(
    val cartId: Long,
    val product: Product,
    val quantity: Int,
)
